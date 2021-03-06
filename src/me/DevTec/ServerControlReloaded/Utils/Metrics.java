package me.DevTec.ServerControlReloaded.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import me.devtec.theapi.TheAPI;
import me.devtec.theapi.scheduler.Tasker;
import me.devtec.theapi.utils.datakeeper.collections.UnsortedList;
import me.devtec.theapi.utils.thapiutils.LoaderClass;

public class Metrics {

	static {
		// You can use the property to disable the check in your test environment
		if (System.getProperty("bstats.relocatecheck") == null
				|| !System.getProperty("bstats.relocatecheck").equals("false")) {
			// Maven's Relocate is clever and changes strings, too. So we have to use this
			// little "trick" ... :D
			final String defaultPackage = new String(
					new byte[] { 'o', 'r', 'g', '.', 'b', 's', 't', 'a', 't', 's', '.', 'b', 'u', 'k', 'k', 'i', 't' });
			final String examplePackage = new String(
					new byte[] { 'y', 'o', 'u', 'r', '.', 'p', 'a', 'c', 'k', 'a', 'g', 'e' });
			// We want to make sure nobody just copy & pastes the example and use the wrong
			// package names
			if (Metrics.class.getPackage().getName().equals(defaultPackage)
					|| Metrics.class.getPackage().getName().equals(examplePackage)) {
				throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
			}
		}
	}

	// The version of this bStats class
	public static final int B_STATS_VERSION = 1;

	// The url to which the data is sent
	private static final String URL = "https://bStats.org/submitData/bukkit";

	// Is bStats enabled on this server?
	private boolean enabled;

	// Should failed requests be logged?
	private static boolean logFailedRequests;

	// Should the sent data be logged?
	private static boolean logSentData;

	// Should the response text be logged?
	private static boolean logResponseStatusText;

	// The uuid of the server
	private static String serverUUID;

	// A list with all custom charts
	private final List<CustomChart> charts = new UnsortedList<>();

	public Metrics() {
		// Load the data
		enabled = true;
		serverUUID = UUID.randomUUID().toString();
		logFailedRequests = false;
		logSentData = false;
		logResponseStatusText = false;

		if (enabled) {
			boolean found = false;
			// Search for all other bStats Metrics classes to see if we are the first one
			for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
				try {
					service.getField("B_STATS_VERSION"); // Our identifier :)
					found = true; // We aren't the first
					break;
				} catch (NoSuchFieldException ignored) {
				}
			}
			// Register our service
			Bukkit.getServicesManager().register(Metrics.class, this, LoaderClass.plugin, ServicePriority.Normal);
			if (!found) {
				// We are the first!
				startSubmitting();
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void addCustomChart(CustomChart chart) {
		if (chart == null) {
			throw new IllegalArgumentException("Chart cannot be null!");
		}
		charts.add(chart);
	}

	private Timer t;
	public Timer getTimer() {
		return t;
	}
	
	private void startSubmitting() {
		t = new Timer(true);
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!LoaderClass.plugin.isEnabled()) {
					t.cancel();
					return;
				}
				new Tasker() {
					@Override
					public void run() {
						submitData();
					}
				}.runTask();
			}
		}, 1000 * 60 * 5, 1000 * 60 * 30);
	}

	@SuppressWarnings("unchecked")
	public JSONObject getPluginData() {
		JSONObject data = new JSONObject();

		String pluginName = LoaderClass.plugin.getDescription().getName();
		String pluginVersion = LoaderClass.plugin.getDescription().getVersion();

		data.put("pluginName", pluginName); // Append the name of the plugin
		data.put("pluginVersion", pluginVersion); // Append the version of the plugin
		JSONArray customCharts = new JSONArray();
		for (CustomChart customChart : charts) {
			// Add the data of the custom charts
			JSONObject chart = customChart.getRequestJsonObject();
			if (chart == null) { // If the chart is null, we skip it
				continue;
			}
			customCharts.add(chart);
		}
		data.put("customCharts", customCharts);

		return data;
	}

	@SuppressWarnings("unchecked")
	private JSONObject getServerData() {
		// Minecraft specific data
		int playerAmount = TheAPI.getOnlinePlayers().size();
		int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
		String bukkitVersion = Bukkit.getVersion();

		// OS/Java specific data
		String javaVersion = System.getProperty("java.version");
		String osName = System.getProperty("os.name");
		String osArch = System.getProperty("os.arch");
		String osVersion = System.getProperty("os.version");
		int coreCount = Runtime.getRuntime().availableProcessors();

		JSONObject data = new JSONObject();

		data.put("serverUUID", serverUUID);

		data.put("playerAmount", playerAmount);
		data.put("onlineMode", onlineMode);
		data.put("bukkitVersion", bukkitVersion);

		data.put("javaVersion", javaVersion);
		data.put("osName", osName);
		data.put("osArch", osArch);
		data.put("osVersion", osVersion);
		data.put("coreCount", coreCount);

		return data;
	}

	@SuppressWarnings("unchecked")
	private void submitData() {
		final JSONObject data = getServerData();

		JSONArray pluginData = new JSONArray();
		// Search for all other bStats Metrics classes to get their plugin data
		for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
			try {
				service.getField("B_STATS_VERSION"); // Our identifier :)

				for (RegisteredServiceProvider<?> provider : Bukkit.getServicesManager().getRegistrations(service)) {
					try {
						pluginData.add(provider.getService().getMethod("getPluginData").invoke(provider.getProvider()));
					} catch (NullPointerException | NoSuchMethodException | IllegalAccessException
							| InvocationTargetException ignored) {
					}
				}
			} catch (NoSuchFieldException ignored) {
			}
		}

		data.put("plugins", pluginData);

		// Create a new thread for the connection to the bStats server
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Send the data
					sendData(LoaderClass.plugin, data);
				} catch (Exception e) {
					// Something went wrong! :(
					if (logFailedRequests) {
						LoaderClass.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + LoaderClass.plugin.getName(),
								e);
					}
				}
			}
		}).start();
	}

	private static void sendData(Plugin plugin, JSONObject data) throws Exception {
		if (data == null) {
			throw new IllegalArgumentException("Data cannot be null!");
		}
		if (Bukkit.isPrimaryThread()) {
			throw new IllegalAccessException("This method must not be called from the main thread!");
		}
		if (logSentData) {
			plugin.getLogger().info("Sending data to bStats: " + data.toString());
		}
		HttpsURLConnection connection = (HttpsURLConnection) new URL(URL).openConnection();

		// Compress the data to save bandwidth
		byte[] compressedData = compress(data.toString());

		// Add headers
		connection.setRequestMethod("POST");
		connection.addRequestProperty("Accept", "application/json");
		connection.addRequestProperty("Connection", "close");
		connection.addRequestProperty("Content-Encoding", "gzip"); // We gzip our request
		connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
		connection.setRequestProperty("Content-Type", "application/json"); // We send our data in JSON format
		connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION);

		// Send data
		connection.setDoOutput(true);
		DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
		outputStream.write(compressedData);
		outputStream.flush();
		outputStream.close();

		InputStream inputStream = connection.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			builder.append(line);
		}
		bufferedReader.close();
		if (logResponseStatusText) {
			plugin.getLogger().info("Sent data to bStats and received response: " + builder.toString());
		}
	}

	private static byte[] compress(final String str) throws IOException {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
		gzip.write(str.getBytes(StandardCharsets.UTF_8));
		gzip.close();
		return outputStream.toByteArray();
	}

	/**
	 * Represents a custom chart.
	 */
	public static abstract class CustomChart {

		// The id of the chart
		final String chartId;

		/**
		 * Class constructor.
		 *
		 * @param chartId The id of the chart.
		 */
		CustomChart(String chartId) {
			if (chartId == null || chartId.isEmpty()) {
				throw new IllegalArgumentException("ChartId cannot be null or empty!");
			}
			this.chartId = chartId;
		}

		@SuppressWarnings("unchecked")
		private JSONObject getRequestJsonObject() {
			JSONObject chart = new JSONObject();
			chart.put("chartId", chartId);
			try {
				JSONObject data = getChartData();
				if (data == null) {
					// If the data is null we don't send the chart.
					return null;
				}
				chart.put("data", data);
			} catch (Throwable t) {
				if (logFailedRequests) {
					Bukkit.getLogger().log(Level.WARNING, "Failed to get data for custom chart with id " + chartId, t);
				}
				return null;
			}
			return chart;
		}

		protected abstract JSONObject getChartData() throws Exception;

	}

	/**
	 * Represents a custom simple pie.
	 */
	public static class SimplePie extends CustomChart {

		private final Callable<String> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId  The id of the chart.
		 * @param callable The callable which is used to request the chart data.
		 */
		public SimplePie(String chartId, Callable<String> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			String value = callable.call();
			if (value == null || value.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			data.put("value", value);
			return data;
		}
	}

	/**
	 * Represents a custom advanced pie.
	 */
	public static class AdvancedPie extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId  The id of the chart.
		 * @param callable The callable which is used to request the chart data.
		 */
		public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			JSONObject values = new JSONObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() == 0) {
					continue; // Skip this invalid
				}
				allSkipped = false;
				values.put(entry.getKey(), entry.getValue());
			}
			if (allSkipped) {
				// Null = skip the chart
				return null;
			}
			data.put("values", values);
			return data;
		}
	}

	/**
	 * Represents a custom drilldown pie.
	 */
	public static class DrilldownPie extends CustomChart {

		private final Callable<Map<String, Map<String, Integer>>> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId  The id of the chart.
		 * @param callable The callable which is used to request the chart data.
		 */
		public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		public JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			JSONObject values = new JSONObject();
			Map<String, Map<String, Integer>> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean reallyAllSkipped = true;
			for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
				JSONObject value = new JSONObject();
				boolean allSkipped = true;
				for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
					value.put(valueEntry.getKey(), valueEntry.getValue());
					allSkipped = false;
				}
				if (!allSkipped) {
					reallyAllSkipped = false;
					values.put(entryValues.getKey(), value);
				}
			}
			if (reallyAllSkipped) {
				// Null = skip the chart
				return null;
			}
			data.put("values", values);
			return data;
		}
	}

	/**
	 * Represents a custom single line chart.
	 */
	public static class SingleLineChart extends CustomChart {

		private final Callable<Integer> callable;

		/**
		 * Class constructor.
		 *
		 * @param chartId  The id of the chart.
		 * @param callable The callable which is used to request the chart data.
		 */
		public SingleLineChart(String chartId, Callable<Integer> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			int value = callable.call();
			if (value == 0) {
				// Null = skip the chart
				return null;
			}
			data.put("value", value);
			return data;
		}

	}

	/**
	 * Represents a custom multi line chart.
	 */
	public static class MultiLineChart extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			JSONObject values = new JSONObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				if (entry.getValue() == 0) {
					continue; // Skip this invalid
				}
				allSkipped = false;
				values.put(entry.getKey(), entry.getValue());
			}
			if (allSkipped) {
				// Null = skip the chart
				return null;
			}
			data.put("values", values);
			return data;
		}

	}

	public static class SimpleBarChart extends CustomChart {

		private final Callable<Map<String, Integer>> callable;

		public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			JSONObject values = new JSONObject();
			Map<String, Integer> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				JSONArray categoryValues = new JSONArray();
				categoryValues.add(entry.getValue());
				values.put(entry.getKey(), categoryValues);
			}
			data.put("values", values);
			return data;
		}

	}

	/**
	 * Represents a custom advanced bar chart.
	 */
	public static class AdvancedBarChart extends CustomChart {

		private final Callable<Map<String, int[]>> callable;

		public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
			super(chartId);
			this.callable = callable;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected JSONObject getChartData() throws Exception {
			JSONObject data = new JSONObject();
			JSONObject values = new JSONObject();
			Map<String, int[]> map = callable.call();
			if (map == null || map.isEmpty()) {
				// Null = skip the chart
				return null;
			}
			boolean allSkipped = true;
			for (Map.Entry<String, int[]> entry : map.entrySet()) {
				if (entry.getValue().length == 0) {
					continue; // Skip this invalid
				}
				allSkipped = false;
				JSONArray categoryValues = new JSONArray();
				for (int categoryValue : entry.getValue()) {
					categoryValues.add(categoryValue);
				}
				values.put(entry.getKey(), categoryValues);
			}
			if (allSkipped) {
				// Null = skip the chart
				return null;
			}
			data.put("values", values);
			return data;
		}
	}

}