package edu.nyu.queryprocessor.util;

import edu.nyu.queryprocessor.entity.Term;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnippetGeneratorTest {
    @Test
    void generateTest() throws IOException {
        String doc = "The hot glowing surfaces of stars emit energy in the form of electromagnetic radiation.?Science & Mathematics PhysicsThe hot glowing surfaces of stars emit energy in the form of electromagnetic radiation.?It is a good approximation to assume that the emissivity e is equal to 1 for these surfaces.Find the radius of the star Rigel, the bright blue star in the constellation Orion that radiates energy at a rate of 2.7 x 10^32 W and has a surface temperature of 11,000 K. Assume that the star is spherical.Use σ =... show moreFollow 3 answersAnswersRelevanceRatingNewestOldestBest Answer: Stefan-Boltzmann law states that the energy flux by radiation is proportional to the forth power of the temperature: q = ε · σ · T^4 The total energy flux at a spherical surface of Radius R is Q = q·π·R² = ε·σ·T^4·π·R² Hence the radius is R = √ ( Q / (ε·σ·T^4·π) ) = √ ( 2.7x10+32 W / (1 · 5.67x10-8W/m²K^4 · (1100K)^4 · π) ) = 3.22x10+13 mSource (s):http://en.wikipedia.org/wiki/Stefan_bolt...schmiso · 1 decade ago0 18 CommentSchmiso, you forgot a 4 in your answer.Your link even says it: L = 4pi (R^2)sigma (T^4).Using L, luminosity, as the energy in this problem, you can find the radius R by doing sqrt (L/ (4pisigma (T^4)).Hope this helps everyone.Caroline · 4 years ago4 1 Comment (Stefan-Boltzmann law) L = 4pi*R^2*sigma*T^4 Solving for R we get: => R = (1/ (2T^2)) * sqrt (L/ (pi*sigma)) Plugging in your values you should get: => R = (1/ (2 (11,000K)^2)) *sqrt ( (2.7*10^32W)/ (pi * (5.67*10^-8 W/m^2K^4))) R = 1.609 * 10^11 m?· 3 years ago0 1 CommentMaybe you would like to learn more about one of these?Want to build a free website?Interested in dating sites?Need a Home Security Safe?How to order contacts online?";
        List<Term> terms = new ArrayList<>();
        terms.add(new Term("hot"));
        terms.add(new Term("glowing"));
        System.out.println(SnippetGenerator.generate(doc, terms));
    }

}