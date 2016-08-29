package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamAreaAtuacao;

public class MamAreaAtuacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamAreaAtuacao> {

	private static final long serialVersionUID = -6283007899651213460L;

	public List<MamAreaAtuacao> listarAreasAtuacaoPorCodigoLogradouroESituacao(Integer codigoLogradouro, DominioSituacao situacao) {
		DetachedCriteria c_atu = DetachedCriteria.forClass(MamAreaAtuacao.class);

		c_atu.add(Restrictions.eq(MamAreaAtuacao.Fields.CODIGO_LOGRADOURO.toString(), codigoLogradouro));
		c_atu.add(Restrictions.eq(MamAreaAtuacao.Fields.SITUACAO.toString(), situacao));

		return executeCriteria(c_atu);
	}

	public List<MamAreaAtuacao> listarAreasAtuacaoPorDescricaoLogradouroESituacao(String descricaoLogradouro, DominioSituacao situacao) {
		DetachedCriteria c_atu_descricao = DetachedCriteria.forClass(MamAreaAtuacao.class);

		c_atu_descricao.add(Restrictions.eq(MamAreaAtuacao.Fields.DESCRICAO_LOGRADOURO.toString(), descricaoLogradouro));
		c_atu_descricao.add(Restrictions.eq(MamAreaAtuacao.Fields.SITUACAO.toString(), situacao));

		return executeCriteria(c_atu_descricao);
	}

	public MamAreaAtuacao obterAreaAtuacaoAtivaPorCodigoLogradouro(Integer codigoLogradouro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAreaAtuacao.class);
		criteria.add(Restrictions.eq(MamAreaAtuacao.Fields.CODIGO_LOGRADOURO.toString(), codigoLogradouro));
		criteria.add(Restrictions.eq(MamAreaAtuacao.Fields.SITUACAO.toString(), DominioSituacao.A));

		List<MamAreaAtuacao> areaAtuacaoList = executeCriteria(criteria, 0, 1, null, true);
		if (areaAtuacaoList != null && !areaAtuacaoList.isEmpty()) {
			return areaAtuacaoList.get(0);
		}
		return null;
	}

	public MamAreaAtuacao obterAreaAtuacaoAtivaPorNomeLogradouro(String logradouro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamAreaAtuacao.class);
		criteria.add(Restrictions.eq(MamAreaAtuacao.Fields.NOME_LOGRADOURO.toString(), logradouro));
		criteria.add(Restrictions.eq(MamAreaAtuacao.Fields.SITUACAO.toString(), DominioSituacao.A));

		List<MamAreaAtuacao> areaAtuacaoList = executeCriteria(criteria, 0, 1, null, true);
		if (areaAtuacaoList != null && !areaAtuacaoList.isEmpty()) {
			return areaAtuacaoList.get(0);
		}
		return null;
	}
}
