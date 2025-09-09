package dev.lrxh.api.features;

import java.util.LinkedHashSet;

import dev.lrxh.api.data.IDivision;

public interface IDivisionService {
    LinkedHashSet<IDivision> getDivisions();
    IDivision getDivisionByElo(int elo);
    void registerDivision(IDivision division);
}
