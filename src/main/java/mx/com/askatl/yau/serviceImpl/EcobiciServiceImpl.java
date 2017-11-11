package mx.com.askatl.yau.serviceImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import mx.com.askatl.yau.entity.StationsStatus;
import mx.com.askatl.yau.repository.StationsStatusRepository;
import mx.com.askatl.yau.service.EcobiciService;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

@Service
public class EcobiciServiceImpl implements EcobiciService {

	@Autowired
	private StationsStatusRepository stationsStatusRepository;
	private static int LIMIT = 500;

	private List<ClassifierInstance> loadDataset(int estacion) {
		final List<ClassifierInstance> instances = Lists.newLinkedList();
		List<StationsStatus> myDatas = stationsStatusRepository.findByIdEstacion(estacion);
		for (StationsStatus myData : myDatas) {
			AttributesMap attributes = AttributesMap.newHashMap();
			float time = myData.getFecha().getHours() + (float) myData.getFecha().getMinutes() / (float) 100;
			attributes.put("time", time);
			instances.add(new ClassifierInstance(attributes, myData.getAvailability().getBikes()));
		}
		return instances;
	}

	@Deprecated
	private double[][] setBikeDataset(int estacion) {
		long total = stationsStatusRepository.countByIdEstacion(estacion);
		double[][] dataset = new double[(int) total][2];
		List<StationsStatus> myData = stationsStatusRepository.findByIdEstacion(estacion);
		for (int i = 0; i < total; i++) {
			dataset[i][0] = myData.get(i).getFecha().getHours()
					+ (float) myData.get(i).getFecha().getMinutes() / (float) 100;
			dataset[i][1] = myData.get(i).getAvailability().getBikes();
		}
		return dataset;
	}

	class ValueComparator implements Comparator<String> {
		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) >= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	private double predict2(int estacion, float time) {
		List<ClassifierInstance> irisDataset = loadDataset(estacion);
		final RandomDecisionForest randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()
				// The default isn't desirable here because this dataset has so few attributes
				.attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.2)))
						.buildPredictiveModel(irisDataset);

		AttributesMap attributes = new AttributesMap();
		attributes.put("time", time);
		//System.out.println("Prediction: " + randomForest.predict(attributes));
		HashMap<String, Double> map = new HashMap<String, Double>();
		ValueComparator bvc = new ValueComparator(map);
		TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);

		for (int i = 0; i < randomForest.predict(attributes).size(); i++) {
			map.put(String.valueOf(i), randomForest.predict(attributes).get(i).doubleValue());
		}
		sorted_map.putAll(map);
		System.out.println("results: " + sorted_map);
		return 0.0;
	}

	public double prediccion(int estacion, float time) {
		System.out.println("e: " + estacion + " t: " + time);

		SimpleRegression regression = new SimpleRegression();
		regression.addData(setBikeDataset(estacion));
		System.out.println("lineal: " + regression.predict(time));
		return predict2(estacion, time);
	}
}
