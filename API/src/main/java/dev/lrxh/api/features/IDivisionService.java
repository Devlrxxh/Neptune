package dev.lrxh.api.features;

import dev.lrxh.api.data.IDivision;

import java.util.LinkedHashSet;

public interface IDivisionService {
    LinkedHashSet<IDivision> getDivisions();

    IDivision getDivisionByElo(int elo);

    void registerDivision(IDivision division);
}
