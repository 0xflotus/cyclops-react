package com.aol.cyclops2.types.foldable;

import com.sun.org.apache.regexp.internal.RE;
import cyclops.collections.mutable.ListX;
import cyclops.stream.ReactiveSeq;
import cyclops.collections.tuple.Tuple;
import cyclops.collections.tuple.Tuple2;
import cyclops.collections.tuple.Tuple3;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;


public class StatCollectorsTest {

    ReactiveSeq<Integer> stats = ReactiveSeq.range(0,100);

    ReactiveSeq<Integer> stats1000 = ReactiveSeq.range(0,1000);


    @Test
    public void atPercentile(){
        assertThat(stats.atPercentile(0),equalTo(0));
        assertThat(stats.atPercentile(1),equalTo(1));
        assertThat(stats.atPercentile(2),equalTo(2));
        assertThat(stats.atPercentile(100),equalTo(99));

    }

    @Test
    public void variance(){

        assertThat(stats.take(10).variance(i->i),equalTo(9.166666666666666));
    }

    @Test
    public void populationVariance(){

        assertThat(stats.take(10).populationVariance(i->i),equalTo(8.25));
    }

    @Test
    public void stdDeviation(){

        assertThat(stats.take(10).stdDeviation(i->i),equalTo(2.8722813232690143));
    }

    @Test
    public void withPercentiles(){
        stats.withPercentiles().limit(4).printOut();
        assertThat(stats.withPercentiles().limit(4).map(t->t.map2(bd->bd.intValue())),equalTo(ListX.of(Tuple.tuple(0,0),
                    Tuple.tuple(1,1),Tuple.tuple(2,2),Tuple.tuple(3,3))));
    }

    @Test
    public void mode(){
        ReactiveSeq<Integer> s = ReactiveSeq.of(1,2,2,2,3,3,3,3,3,4,4,4);
        assertThat(s.mode().get(),equalTo(3));
    }
    @Test
    public void maxBy(){
        ReactiveSeq<Integer> s = ReactiveSeq.of(1,2,2,2,3,3,3,3,3,4,4,4);
        assertThat(s.maxBy(i->i).get(),equalTo(4));
    }

    @Test
    public void doubleCollect(){
        Tuple2<List<Integer>, Set<Integer>> t2 = stats.collect(Collectors.toList(), Collectors.toSet());
        assertThat(t2._1(),equalTo(stats.toList()));
        assertThat(t2._2(),equalTo(stats.toSet()));
    }
    @Test
    public void tripleCollect(){
        Tuple3<List<Integer>, Set<Integer>,List<Integer>> t3 = stats.collect(Collectors.toList(), Collectors.toSet(),Collectors.toList());
        assertThat(t3._1(),equalTo(stats.toList()));
        assertThat(t3._2(),equalTo(stats.toSet()));
        assertThat(t3._3(),equalTo(stats.toList()));
    }

}