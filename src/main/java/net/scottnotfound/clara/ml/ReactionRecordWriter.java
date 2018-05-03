package net.scottnotfound.clara.ml;

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.writer.RecordWriter;
import org.datavec.api.split.InputSplit;
import org.datavec.api.split.partition.PartitionMetaData;
import org.datavec.api.split.partition.Partitioner;
import org.datavec.api.writable.Writable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ReactionRecordWriter implements RecordWriter {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String NEW_LINE = "\n";

    protected DataOutputStream out;
    protected Charset encoding = DEFAULT_CHARSET;
    protected Partitioner partitioner;
    protected Configuration conf;

    public ReactionRecordWriter() {}


    @Override
    public boolean supportsBatch() {
        return false;
    }

    @Override
    public void initialize(InputSplit inputSplit, Partitioner partitioner) throws Exception {
        partitioner.init(inputSplit);
        out = new DataOutputStream(partitioner.currentOutputStream());
        this.partitioner = partitioner;
    }

    @Override
    public void initialize(Configuration configuration, InputSplit split, Partitioner partitioner) throws Exception {
        setConf(configuration);
        partitioner.init(configuration, split);
        initialize(split, partitioner);
    }

    @Override
    public PartitionMetaData write(List<Writable> record) throws IOException {
        if (!record.isEmpty()) {
            ReactionWritable r = (ReactionWritable) record.iterator().next();
            r.write(out);
            out.write(NEW_LINE.getBytes());
        }
        return PartitionMetaData.builder().numRecordsUpdated(1).build();
    }

    @Override
    public PartitionMetaData writeBatch(List<List<Writable>> batch) throws IOException {
        for (List<Writable> record : batch) {
            ReactionWritable r = (ReactionWritable) record.iterator().next();
            try {
                r.write(out);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
        return PartitionMetaData.builder().numRecordsUpdated(1).build();
    }

    @Override
    public void close() {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = conf;
    }

    @Override
    public Configuration getConf() {
        return conf;
    }

}
