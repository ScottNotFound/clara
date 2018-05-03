package net.scottnotfound.clara.ml;

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.split.InputSplit;
import org.datavec.api.split.partition.PartitionMetaData;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.writable.Writable;

import java.io.IOException;
import java.util.List;

public class ReactionRecordWriter implements RecordWriter {



    /**
     * Returns true if this record writer
     * supports efficient batch writing using {@link #writeBatch(List)}
     *
     * @return
     */
    @Override
    public boolean supportsBatch() {
        return false;
    }

    /**
     * Initialize a record writer with the given input split
     *
     * @param inputSplit  the input split to initialize with
     * @param partitioner
     */
    @Override
    public void initialize(InputSplit inputSplit, Partitioner partitioner) throws Exception {

    }

    /**
     * Initialize the record reader with the given configuration
     * and {@link InputSplit}
     *
     * @param configuration the configuration to iniailize with
     * @param split         the split to use
     * @param partitioner
     */
    @Override
    public void initialize(Configuration configuration, InputSplit split, Partitioner partitioner) throws Exception {

    }

    /**
     * Write a record
     *
     * @param record the record to write
     */
    @Override
    public PartitionMetaData write(List<Writable> record) throws IOException {
        return null;
    }

    /**
     * Write a batch of records
     *
     * @param batch the batch to write
     */
    @Override
    public PartitionMetaData writeBatch(List<List<Writable>> batch) throws IOException {
        return null;
    }

    /**
     * Close the recod reader
     */
    @Override
    public void close() {

    }

    /**
     * Set the configuration to be used by this object.
     *
     * @param conf
     */
    @Override
    public void setConf(Configuration conf) {

    }

    /**
     * Return the configuration used by this object.
     */
    @Override
    public Configuration getConf() {
        return null;
    }

}
