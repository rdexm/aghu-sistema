package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class BloqueioConsultaON extends BaseBusiness {

	private static final long serialVersionUID = 5932661676766248094L;

	private static final Log LOG = LogFactory.getLog(BloqueioConsultaON.class);

	public AacSituacaoConsultas verificarPreenchimentoAusenciaProfissional(AacSituacaoConsultas registro, DominioSimNao dominioSimNao) {
		if (dominioSimNao == null || dominioSimNao == DominioSimNao.N){
			registro.setAusenciaProfissional(false);
		} else if (dominioSimNao == DominioSimNao.S) {
			registro.setAusenciaProfissional(true);
		}
		return registro;
	}

	public AacSituacaoConsultas verificarPreenchimentoSituacao(AacSituacaoConsultas registro, DominioSituacao situacao) {
		if (situacao == null) {
			registro.setIndSituacao(DominioSituacao.A);
		} else {
			registro.setIndSituacao(situacao);
		}
		return registro;
	}

	public AacSituacaoConsultas verificarPreenchimentoAusenciaProfSituacao(AacSituacaoConsultas registro, DominioSituacao situacao,
			DominioSimNao dominioSimNao) {
		registro = verificarPreenchimentoAusenciaProfissional(registro, dominioSimNao);
		registro = verificarPreenchimentoSituacao(registro, situacao);
		registro.setBloqueio(DominioSimNao.getInstance(true).isSim());
		return registro;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}
