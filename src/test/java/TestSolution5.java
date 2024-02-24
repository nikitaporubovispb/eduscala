import org.junit.Assert;
import org.junit.Test;
import org.pnp.Solution5;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestSolution5 {

    // Solution5.isDone
    @Test
    public void testIsDoneNoIntervals() {
        Assert.assertFalse(Solution5.isDone(3, Collections.emptyList()));
    }

    @Test
    public void testIsDoneNotWholeArray() {
        Assert.assertFalse(Solution5.isDone(3, List.of(new Solution5.Interval(1, 2, 2))));
    }

    @Test
    public void testIsDoneWholeArray() {
        Assert.assertTrue(Solution5.isDone(3, List.of(new Solution5.Interval(1, 3, 3))));
    }

    @Test
    public void testIsDoneTwoIntervalNotWholeArrayIntersect() {
        Assert.assertFalse(Solution5.isDone(3,
                List.of(new Solution5.Interval(1, 2, 2),
                        new Solution5.Interval(2, 3, 2))));
    }

    @Test
    public void testIsDoneTwoIntervalNotWholeArray() {
        Assert.assertFalse(Solution5.isDone(3,
                List.of(new Solution5.Interval(1, 1, 1),
                        new Solution5.Interval(3, 3, 1))));
    }

    @Test
    public void testIsDoneTwoIntervalWholeArrayStart() {
        Assert.assertTrue(Solution5.isDone(3,
                List.of(new Solution5.Interval(1, 1, 1),
                        new Solution5.Interval(2, 3, 2))));
    }

    @Test
    public void testIsDoneTwoIntervalWholeArrayEnd() {
        Assert.assertTrue(Solution5.isDone(3,
                List.of(new Solution5.Interval(1, 2, 2),
                        new Solution5.Interval(3, 3, 1))));
    }

    // Solution5.makeMapOfSubs
    @Test
    public void testMakeMapOfSubsNoIntervals() {
        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(Collections.emptyList());
        Assert.assertTrue(intervalListMap.isEmpty());
    }

    @Test
    public void testMakeMapOfSubsOneInterval() {
        Solution5.Interval i1 = new Solution5.Interval(1, 2, 2);
        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(
                List.of(i1));
        Assert.assertTrue(intervalListMap.get(i1).isEmpty());
    }

    @Test
    public void testMakeMapOfSubsTwoInterval() {
        Solution5.Interval i1 = new Solution5.Interval(1, 2, 2);
        Solution5.Interval i2 = new Solution5.Interval(2, 3, 2);

        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(
                List.of(i1, i2));

        Assert.assertTrue(intervalListMap.get(i1).isEmpty());
        Assert.assertTrue(intervalListMap.get(i2).isEmpty());
    }

    @Test
    public void testMakeMapOfSubsInlineIntervalStart() {
        Solution5.Interval i1 = new Solution5.Interval(1, 3, 3);
        Solution5.Interval i2 = new Solution5.Interval(1, 2, 2);

        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(
                List.of(i1, i2));

        Assert.assertEquals(i2, intervalListMap.get(i1).get(0));
    }

    @Test
    public void testMakeMapOfSubsInlineIntervalMiddle() {
        Solution5.Interval i1 = new Solution5.Interval(1, 3, 3);
        Solution5.Interval i2 = new Solution5.Interval(2, 2, 1);

        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(
                List.of(i1, i2));

        Assert.assertEquals(i2, intervalListMap.get(i1).get(0));
    }

    @Test
    public void testMakeMapOfSubsInlineIntervalEnd() {
        Solution5.Interval i1 = new Solution5.Interval(1, 3, 3);
        Solution5.Interval i2 = new Solution5.Interval(2, 3, 2);

        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(
                List.of(i1, i2));

        Assert.assertEquals(i2, intervalListMap.get(i1).get(0));
    }

    @Test
    public void testMakeMapOfSubsInlineTripleInterval() {
        Solution5.Interval i1 = new Solution5.Interval(1, 3, 3);
        Solution5.Interval i2 = new Solution5.Interval(2, 3, 2);
        Solution5.Interval i3 = new Solution5.Interval(3, 3, 2);

        Map<Solution5.Interval, List<Solution5.Interval>> intervalListMap = Solution5.makeMapOfSubs(
                List.of(i1, i2, i3));

        Assert.assertEquals(i2, intervalListMap.get(i1).get(0));
        Assert.assertEquals(i3, intervalListMap.get(i1).get(1));
        Assert.assertEquals(i3, intervalListMap.get(i2).get(0));
        Assert.assertTrue(intervalListMap.get(i3).isEmpty());
    }
}
