package services;

import models.Thesis;
import repositories.ThesisRepository;

import java.sql.SQLException;
import java.util.List;

public class ThesisService {

    private final ThesisRepository repository = new ThesisRepository();

    public List<Thesis> getAll() throws SQLException {
        return repository.findAll();
    }

    public Thesis getById(String maDT) throws SQLException {
        return repository.findById(maDT);
    }

    public boolean exists(String maDT) throws SQLException {
        return repository.exists(maDT);
    }

    public void create(Thesis thesis) throws SQLException {
        repository.create(thesis);
    }

    public boolean update(String maDT, Thesis thesis) throws SQLException {
        return repository.update(maDT, thesis);
    }

    public boolean delete(String maDT) throws SQLException {
        return repository.delete(maDT);
    }
}