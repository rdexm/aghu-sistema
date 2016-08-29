package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MpmNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class MpmNotaAdicionalEvolucoesRN extends BaseBusiness {

	private static final long serialVersionUID = 156788990909089L;
	private static final Log LOG = LogFactory
			.getLog(MpmNotaAdicionalEvolucoesRN.class);
	
	@Inject
	private MpmNotaAdicionalEvolucoesDAO mpmNotaAdicionalEvolucoesDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final String TRACO = " - ";
	private static final String DOIS_PONTOS = ": ";

	public void persistirNotaAdicionalEvolucoes(
			MpmNotaAdicionalEvolucoes notaAdicional, String descricao, String nomeEspecialidade) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		String descricaoNotaAdicionalAux = servidorLogado.getPessoaFisica().getNome()
				.concat(TRACO).concat(nomeEspecialidade).concat(DOIS_PONTOS)
				.concat(descricao);
		notaAdicional.setPendente(DominioIndPendenteAmbulatorio.V);
		notaAdicional.setDthrCriacao(new Date());
		notaAdicional.setDescricao(descricaoNotaAdicionalAux);
		notaAdicional.setServidor(servidorLogado);
		getMpmNotaAdicionalEvolucoesDAO().persistir(notaAdicional);
	}

	private MpmNotaAdicionalEvolucoesDAO getMpmNotaAdicionalEvolucoesDAO() {
		return mpmNotaAdicionalEvolucoesDAO;
	}

}
