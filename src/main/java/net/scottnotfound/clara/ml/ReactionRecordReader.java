package net.scottnotfound.clara.ml;

import org.datavec.api.conf.Configuration;
import org.datavec.api.records.Record;
import org.datavec.api.records.listener.RecordListener;
import org.datavec.api.records.metadata.RecordMetaData;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.InputSplit;
import org.datavec.api.writable.Writable;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

public class ReactionRecordReader implements RecordReader {


    /**
     * Called once at initialization.
     *
     * @param split the split that defines the range of records to read
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void initialize(InputSplit split) throws IOException, InterruptedException {

    }

    /**
     * Called once at initialization.
     *
     * @param conf  a configuration for initialization
     * @param split the split that defines the range of records to read
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void initialize(Configuration conf, InputSplit split) throws IOException, InterruptedException {

    }

    /**
     * This method returns true, if next(int) signature is supported by this RecordReader implementation.
     *
     * @return
     */
    @Override
    public boolean batchesSupported() {
        return false;
    }

    /**
     * This method will be used, if batchesSupported() returns true.
     *
     * @param num
     * @return
     */
    @Override
    public List<List<Writable>> next(int num) {
        return null;
    }

    /**
     * Get the next record
     *
     * @return
     */
    @Override
    public List<Writable> next() {
        return null;
    }

    /**
     * Whether there are anymore records
     *
     * @return
     */
    @Override
    public boolean hasNext() {
        return false;
    }

    /**
     * List of label strings
     *
     * @return
     */
    @Override
    public List<String> getLabels() {
        return null;
    }

    /**
     * Reset record reader iterator
     *
     * @return
     */
    @Override
    public void reset() {

    }

    /**
     * @return True if the record reader can be reset, false otherwise. Note that some record readers cannot be reset -
     * for example, if they are backed by a non-resettable input split (such as certain types of streams)
     */
    @Override
    public boolean resetSupported() {
        return false;
    }

    /**
     * Load the record from the given DataInputStream
     * Unlike {@link #next()} the internal state of the RecordReader is not modified
     * Implementations of this method should not close the DataInputStream
     *
     * @param uri
     * @param dataInputStream
     * @throws IOException if error occurs during reading from the input stream
     */
    @Override
    public List<Writable> record(URI uri, DataInputStream dataInputStream) throws IOException {
        return null;
    }

    /**
     * Similar to {@link #next()}, but returns a {@link Record} object, that may include metadata such as the source
     * of the data
     *
     * @return next record
     */
    @Override
    public Record nextRecord() {
        return null;
    }

    /**
     * Load a single record from the given {@link RecordMetaData} instance<br>
     * Note: that for data that isn't splittable (i.e., text data that needs to be scanned/split), it is more efficient to
     * load multiple records at once using {@link #loadFromMetaData(List)}
     *
     * @param recordMetaData Metadata for the record that we want to load from
     * @return Single record for the given RecordMetaData instance
     * @throws IOException If I/O error occurs during loading
     */
    @Override
    public Record loadFromMetaData(RecordMetaData recordMetaData) throws IOException {
        return null;
    }

    /**
     * Load multiple records from the given a list of {@link RecordMetaData} instances<br>
     *
     * @param recordMetaDatas Metadata for the records that we want to load from
     * @return Multiple records for the given RecordMetaData instances
     * @throws IOException If I/O error occurs during loading
     */
    @Override
    public List<Record> loadFromMetaData(List<RecordMetaData> recordMetaDatas) throws IOException {
        return null;
    }

    /**
     * Get the record listeners for this record reader.
     */
    @Override
    public List<RecordListener> getListeners() {
        return null;
    }

    /**
     * Set the record listeners for this record reader.
     *
     * @param listeners
     */
    @Override
    public void setListeners(RecordListener... listeners) {

    }

    /**
     * Set the record listeners for this record reader.
     *
     * @param listeners
     */
    @Override
    public void setListeners(Collection<RecordListener> listeners) {

    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {

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
