package net.scottnotfound.clara.ml;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

/**
 * This class is a proxy for the Configurations in DL4J to simplify
 * the network building for chemical reaction purposes.
 */
class NetworkConfiguration {

    private Builder builder;

    private NetworkConfiguration(Builder builder) {
        this.builder = builder;
    }

    static class Builder {

        private boolean         seeded          = false;
        private boolean         backprop        = true;

        private int             seed            = 0;
        private int             inputDim        = 4096;
        private int             outputDim       = 1;
        private int             numHiddenLayers = 5;
        private int             hiddenLayerDim  = 512;

        private LossFunction    lossFunction    = LossFunction.NEGATIVELOGLIKELIHOOD;
        private Activation      hiddenAct       = Activation.ELU;
        private Activation      initialAct      = Activation.ELU;
        private Activation      outputAct       = Activation.SIGMOID;
        private WeightInit      weightInit      = WeightInit.XAVIER;


        NetworkConfiguration build() {
            return new NetworkConfiguration(this);
        }

        /** Seed to initialize random weights. */
        Builder seed(int seed) {
            this.seeded = true;
            this.seed = seed;
            return this;
        }

        /** Number of input dimensions. */
        Builder nIn(int inputDim) {
            this.inputDim = inputDim;
            return this;
        }

        /** Number of output dimensions. */
        Builder nOut(int outputDim) {
            this.outputDim = outputDim;
            return this;
        }

        /** Number and dimension of fully connected hidden dense layers. */
        Builder denseLayers(int num, int dim) {
            this.numHiddenLayers = num;
            this.hiddenLayerDim = dim;
            return this;
        }

        /** Loss function to use. */
        Builder lossFunction(LossFunction lossFunction) {
            this.lossFunction = lossFunction;
            return this;
        }

        /** Activation function for the input layer. */
        Builder initialActivation(Activation activation) {
            this.initialAct = activation;
            return this;
        }

        /** Activation function for the output layer. */
        Builder outputActivation(Activation activation) {
            this.outputAct = activation;
            return this;
        }

        /** Activation function for the hidden layers. */
        Builder hiddenActivation(Activation activation) {
            this.hiddenAct = activation;
            return this;
        }

        /** Method if initializing the random weights. */
        Builder weightInit(WeightInit weightInit) {
            this.weightInit = weightInit;
            return this;
        }

    }

    /**
     * Makes a MultiLayerConfiguration from the set values.
     */
    MultiLayerConfiguration makeMultiLayerConfiguration() {

        return new MultiLayerConfiguration.Builder().build();

    }


}
