package br.gov.mec.aghu.business.bancosangue;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;

@Stateless
public class GrupoJustificativaHemoterapiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoJustificativaHemoterapiaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 2590735878861924360L;

	/**
	 * @ORADB ABST_GJC_BRI
	 * 
	 * Metodo que irá atualizar os dados do Grupo de Justificativa do Componente Sanguineo para persistir na base de dados
	 * 
	 * RN1: Seta ser_vin_codigo e ser_matricula de acordo com o usuário logado.
	 * RN2: Seta CRIADO_EM e ALTERADO_EM com a data atual.
	 * 
	 * @param grupo
	 * @throws ApplicationBusinessException  
	 */
	public void preInsertGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupo.setServidor(servidorLogado);
		grupo.setCriadoEm(new Date());
		//grupo.setAlteradoEm(new Date());
	}

	/**
	 * @ORADB ABST_GJC_BRU
	 * 
	 * Metodo que irá atualizar os dados do Grupo de Justificativa do Componente Sanguineo para atualizar na base de dados
	 * 
	 * RN1: Seta ALTERADO_EM com a data atual.
	 * RN2: Seta ser_vin_codigo_alterado e ser_matricula_alterado de acordo com o usuário logado.
	 * 
	 * @param grupo
	 * @throws ApplicationBusinessException  
	 */
	public void preUpdateGrupoJustificativaComponenteSanguineo(AbsGrupoJustificativaComponenteSanguineo grupo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupo.setServidorAlterado(servidorLogado);
		grupo.setAlteradoEm(new Date());
	}

	
	/**
	 * @ORADB ABST_JCS_BRI
	 * Metodo que irá completar os dados da Justificativa do Componente Sanguineo para persistir na base de dados.
	 * 
	 * RN1: Seta ser_vin_codigo e ser_matricula de acordo com o usuário logado.
	 * RN2: Seta CRIADO_EM e ALTERADO_EM com a data atual.
	 * 
	 * @param absJustificativaComponenteSanguineo
	 * @throws ApplicationBusinessException  
	 */
	public void preInsertJustificativaComponenteSanquineo(AbsJustificativaComponenteSanguineo justificativa) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		justificativa.setServidor(servidorLogado);
		justificativa.setCriadoEm(new Date());
		justificativa.setAlteradoEm(new Date());
	}
	
	/**
	 * @ORADB ABST_JCS_BRU
	 * 
	 * Metodo que irá completar os dados da Justificativa do Componente Sanguineo para atualizar na base de dados.
	 * 
	 * RN1: Seta ALTERADO_EM com a data atual.
	 * RN2: Seta ser_vin_codigo_alterado e ser_matricula_alterado de acordo com o usuário logado.
	 * 
	 * @param justificativa
	 * @throws BaseListException
	 * @throws ApplicationBusinessException  
	 */
	public void preUpdateJustificativaComponenteSanguineo(AbsJustificativaComponenteSanguineo justificativa) throws BaseListException, ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		justificativa.setServidorAlterado(servidorLogado);
		justificativa.setAlteradoEm(new Date());
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
