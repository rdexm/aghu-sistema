package br.gov.mec.aghu.exames.patologia.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelCadGuiche;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class IdentificaGuicheON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(IdentificaGuicheON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	private static final long serialVersionUID = -4353396880880677258L;
	
	/**
	 * AELF_IDENTIF_GUICHES.FMB
	 * 02-BUT-SELECIONA_WHEN-BUTTON-PRESSED.sql
	 * fonte: http://websvn.mec.gov.br/svn/aghu/documentos/Analise/Sprint51/LaranjaMecanica/5444/sql/
	 */
	public void persistirIdentificacaoGuiche(final AelCadGuiche guiche, final AghUnidadesFuncionais unidade, final DominioSituacao situacao, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelCadGuiche guicheUser = getExamesPatologiaFacade().obterAelCadGuichePorUsuarioUnidadeExecutora(unidade, servidorLogado != null ? servidorLogado.getUsuario() : null, situacao, guiche.getSeq());
		if(guicheUser != null){
			guicheUser.setOcupado(DominioSimNao.N);
			getExamesPatologiaFacade().alterarAelCadGuiche(guicheUser, nomeMicrocomputador);
		}
		
		if(DominioSimNao.S.equals(guiche.getOcupado())){
			
			//guiche.setOcupado(DominioSimNao.N);
			//getExamesPatologiaFacade().alterarAelCadGuiche(guiche, nomeMicrocomputador);

			/*
			  --
	          -- loop para garantir data diferente(pk) na tabela
	          -- movimento guiche
	          --
	          FOR i IN 1..2500000 LOOP
	          	v_cont := v_cont + 1;
	          END LOOP;
			 */
			
			guiche.setOcupado(DominioSimNao.S);
			getExamesPatologiaFacade().alterarAelCadGuiche(guiche, nomeMicrocomputador);
			
		} else {
			guiche.setOcupado(DominioSimNao.S);
			getExamesPatologiaFacade().alterarAelCadGuiche(guiche, nomeMicrocomputador);
		}
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return this.examesPatologiaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}