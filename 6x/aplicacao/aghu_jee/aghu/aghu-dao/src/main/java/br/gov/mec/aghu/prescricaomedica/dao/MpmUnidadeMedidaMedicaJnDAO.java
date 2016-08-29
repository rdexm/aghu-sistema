package br.gov.mec.aghu.prescricaomedica.dao;

import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedicaJn;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

public class MpmUnidadeMedidaMedicaJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmUnidadeMedidaMedicaJn> {

	private static final long serialVersionUID = -1662428253920675027L;

	public void persistirUnidadeMedidaMedicaJn(MpmUnidadeMedidaMedica unidade,
			DominioOperacoesJournal operacao, String usuarioLogado) {
		MpmUnidadeMedidaMedicaJn unidadeJN = prepareUnidadeMedidaMedicaJn(
				unidade, operacao, usuarioLogado);
		this.persistir(unidadeJN);
		this.flush();
	}

	private MpmUnidadeMedidaMedicaJn prepareUnidadeMedidaMedicaJn(
			MpmUnidadeMedidaMedica unidade, DominioOperacoesJournal operacao, String usuarioLogado) {
		
		MpmUnidadeMedidaMedicaJn jn = BaseJournalFactory.getBaseJournal(operacao, MpmUnidadeMedidaMedicaJn.class, usuarioLogado);

		jn.setDescricao(unidade.getDescricao());
		jn.setCriadoEm(unidade.getCriadoEm());
		jn.setSeq(unidade.getSeq());
		jn.setSerMatricula(unidade.getServidor().getId().getMatricula());
		jn.setSerVinCodigo(unidade.getServidor().getId().getVinCodigo());

		jn.setIndConcentracao(unidade.getIndConcentracao().toString());
		jn.setIndConcentracaoAlvo(unidade.getIndConcentracaoAlvo());
		jn.setIndMonitHemodinamica(unidade.getIndMonitHemodinamica().toString());
		jn.setIndPrescricaoDose(unidade.getIndPrescricaoDose().toString());
		jn.setIndSituacao(unidade.getIndSituacao().toString());
		jn.setIndUsoDialise(unidade.getIndUsoDialise().toString());
		jn.setIndUsoNutricao(unidade.getIndUsoNutricao().toString());
		jn.setIndVolumeNpt(unidade.getIndVolumeNpt().toString());

		return jn;
	}

}
