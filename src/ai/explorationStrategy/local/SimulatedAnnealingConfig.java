package ai.explorationStrategy.local;

public class SimulatedAnnealingConfig extends LocalSearchConfig {

    private double initialTemperature = 5;

    private double temperatureDecrease = 0.95;

    public SimulatedAnnealingConfig() { }

    public SimulatedAnnealingConfig(int neighbourTestLimit, double initialTemperature, double temperatureDecrease, int iterationLimit) {
        this.neighbourTestLimit = neighbourTestLimit;
        this.initialTemperature = initialTemperature;
        this.temperatureDecrease = temperatureDecrease;
        this.iterationLimit = iterationLimit;
    }

    public boolean stopLoop(int iterationCount, int neighbourTestCount, double t) {
        return neighbourTestCount > neighbourTestLimit || iterationCount > iterationLimit || t < 0.1;
    }

    public double getInitialTemperature() {
        return initialTemperature;
    }

    public void setInitialTemperature(double initialTemperature) {
        this.initialTemperature = initialTemperature;
    }

    public double getTemperatureDecrease() {
        return temperatureDecrease;
    }

    public void setTemperatureDecrease(double temperatureDecrease) {
        this.temperatureDecrease = temperatureDecrease;
    }

    public double decreaseTemperature(double t) {
        return t * temperatureDecrease;
    }
}
