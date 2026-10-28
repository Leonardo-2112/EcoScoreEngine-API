package br.com.soulprime.bo;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.soulprime.dao.NivelDao;
import br.com.soulprime.model.Nivel;

//Niveis de progressao (tabela de dominio definida pela regra de negocio)
@Service
public class NivelBo {

    private final NivelDao nivelDao;

    public NivelBo(NivelDao nivelDao) {
        this.nivelDao = nivelDao;
    }

    public List<Nivel> listar() throws SQLException {
        return nivelDao.listar();
    }
}
