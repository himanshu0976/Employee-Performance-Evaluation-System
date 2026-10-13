package com.eps.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationTest {

    @Test
    public void testDetermineRatingThresholds() {
        assertEquals("Outstanding", Evaluation.determineRating(new BigDecimal("5.00")));
        assertEquals("Outstanding", Evaluation.determineRating(new BigDecimal("4.50")));
        assertEquals("Exceeds Expectations", Evaluation.determineRating(new BigDecimal("4.49")));
        assertEquals("Exceeds Expectations", Evaluation.determineRating(new BigDecimal("3.50")));
        assertEquals("Meets Expectations", Evaluation.determineRating(new BigDecimal("3.49")));
        assertEquals("Meets Expectations", Evaluation.determineRating(new BigDecimal("2.50")));
        assertEquals("Needs Improvement", Evaluation.determineRating(new BigDecimal("2.49")));
        assertEquals("Needs Improvement", Evaluation.determineRating(new BigDecimal("1.50")));
        assertEquals("Unsatisfactory", Evaluation.determineRating(new BigDecimal("1.49")));
        assertEquals("Unsatisfactory", Evaluation.determineRating(new BigDecimal("1.00")));
    }

    @Test
    public void testCalculateTotalScoreAndRatingWeighted() {
        Evaluation eval = new Evaluation();
        List<EvaluationScore> scores = new ArrayList<>();

        // Attendance: weight 15%, score 5.0 -> 0.75
        // Work Quality: weight 20%, score 5.0 -> 1.00
        // Productivity: weight 20%, score 4.5 -> 0.90
        // Teamwork: weight 15%, score 5.0 -> 0.75
        // Communication: weight 15%, score 4.0 -> 0.60
        // Goal Achievement: weight 15%, score 4.5 -> 0.675
        // Total weighted sum = 4.675 / 100 = 4.68 (Outstanding)
        scores.add(createScore(1, new BigDecimal("15.00"), new BigDecimal("5.0")));
        scores.add(createScore(2, new BigDecimal("20.00"), new BigDecimal("5.0")));
        scores.add(createScore(3, new BigDecimal("20.00"), new BigDecimal("4.5")));
        scores.add(createScore(4, new BigDecimal("15.00"), new BigDecimal("5.0")));
        scores.add(createScore(5, new BigDecimal("15.00"), new BigDecimal("4.0")));
        scores.add(createScore(6, new BigDecimal("15.00"), new BigDecimal("4.5")));

        eval.setScores(scores);
        eval.calculateTotalScoreAndRating();

        assertNotNull(eval.getTotalScore());
        assertEquals(new BigDecimal("4.68"), eval.getTotalScore());
        assertEquals("Outstanding", eval.getRating());
        assertEquals("bg-success", eval.getRatingBadgeClass());
    }

    private EvaluationScore createScore(int criteriaId, BigDecimal weight, BigDecimal score) {
        EvaluationScore s = new EvaluationScore();
        s.setCriteriaId(criteriaId);
        s.setWeightage(weight);
        s.setScore(score);
        return s;
    }
}
