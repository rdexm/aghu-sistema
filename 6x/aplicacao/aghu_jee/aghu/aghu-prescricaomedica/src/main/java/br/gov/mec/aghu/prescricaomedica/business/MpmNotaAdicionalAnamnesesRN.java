package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MpmNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;

@Stateless
public class MpmNotaAdicionalAnamnesesRN extends BaseBusiness {

	private static final long serialVersionUID = 123456787897L;
	private static final Log LOG = LogFactory
			.getLog(MpmNotaAdicionalAnamnesesRN.class);
	
	@Inject
	private MpmNotaAdicionalAnamnesesDAO mpmNotaAdicionalAnamnesesDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final String TRACO = " - ";
	private static final String DOIS_PONTOS = ": ";

	public void persistirMpmNotaAdicionalAnamneses(
			MpmNotaAdicionalAnamneses notaAdicional, String descricao,
			RapServidores servidor, String nomeEspecialidade) {
		
		servidor = rapServidoresDAO.obterServidor(servidor);
		
		String nomePessoaFisica = servidor.getPessoaFisica().getNome(); 
		String descricaoNotaAdicionalAux = nomePessoaFisica
				.concat(TRACO).concat(nomeEspecialidade).concat(DOIS_PONTOS)
				.concat(descricao);
		notaAdicional.setPendente(DominioIndPendenteAmbulatorio.V);
		notaAdicional.setDthrCriacao(new Date());
		notaAdicional.setDescricao(descricaoNotaAdicionalAux);
		notaAdicional.setServidor(servidor);
		getMpmNotaAdicionalAnamnesesDAO().persistir(notaAdicional);

	}

	private MpmNotaAdicionalAnamnesesDAO getMpmNotaAdicionalAnamnesesDAO() {
		return mpmNotaAdicionalAnamnesesDAO;
	}

}
