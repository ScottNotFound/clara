package net.scottnotfound.clara.ml;

import org.datavec.api.writable.Writable;
import org.datavec.api.writable.WritableType;
import org.openscience.cdk.interfaces.IReaction;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReactionWritable implements Writable {

    private IReaction reaction;


    public ReactionWritable() {
        this.reaction = null;
    }

    public ReactionWritable(IReaction reaction) {
        this.reaction = reaction;
    }


    @Override
    public void write(DataOutput out) throws IOException {

    }

    @Override
    public void readFields(DataInput in) throws IOException {

    }

    @Override
    public void writeType(DataOutput out) throws IOException {

    }

    @Override
    public double toDouble() {
        return 0;
    }

    @Override
    public float toFloat() {
        return 0;
    }

    @Override
    public int toInt() {
        return 0;
    }

    @Override
    public long toLong() {
        return 0;
    }

    @Override
    public WritableType getType() {
        return null;
    }

}
