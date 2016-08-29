package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelHorarioRotinaColetasDAO;
import br.gov.mec.aghu.exames.dao.AelPermissaoUnidSolicDAO;
import br.gov.mec.aghu.model.AelHorarioRotinaColetas;
import br.gov.mec.aghu.model.AelHorarioRotinaColetasId;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterHorarioRotinaColetaCRUD extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ManterHorarioRotinaColetaCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelHorarioRotinaColetasDAO aelHorarioRotinaColetasDAO;
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@Inject
	private AelPermissaoUnidSolicDAO aelPermissaoUnidSolicDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5087501376105407858L;

	public AelHorarioRotinaColetas obterAelHorarioRotinaColetaPorId(AelHorarioRotinaColetasId horarioId){
		return getAelHorarioRotinaColetasDAO().obterAelHorarioRotinaColetaPorId(horarioId);
	}

	public List<AelHorarioRotinaColetas> obterAelHorarioRotinaColetasPorParametros(Short unidadeColeta, Short unidadeSolicitante, Date dataHora, String dia, DominioSituacao situacaoHorario){
		return getAelHorarioRotinaColetasDAO().obterAelHorarioRotinaColetasPorParametros(unidadeColeta, unidadeSolicitante, dataHora, dia, situacaoHorario);
	}

	public void persistir(AelHorarioRotinaColetas horarioRotina) throws ApplicationBusinessException{
		preInsert(horarioRotina);
		AelHorarioRotinaColetasDAO dao = getAelHorarioRotinaColetasDAO();
		dao.persistir(horarioRotina);
		dao.flush();
		dao.desatachar(horarioRotina);
	}

	public void excluirHorarioRotina(AelHorarioRotinaColetasId horarioRotinaId) throws ApplicationBusinessException{
		AelHorarioRotinaColetasDAO dao = getAelHorarioRotinaColetasDAO();
		AelHorarioRotinaColetas horRotExc = dao.obterAelHorarioRotinaColetaPorId(horarioRotinaId);

		verificaUltimoHorarioUnidade(horRotExc, false);

		dao.remover(horRotExc);
		dao.flush();
	}

	public void atualizar(AelHorarioRotinaColetas horarioRotina) throws ApplicationBusinessException{
		preUpdate(horarioRotina);
		verificaUltimoHorarioUnidade(horarioRotina, true);

		/*tenho inserir e depois excluir o antigo*/
		AelHorarioRotinaColetasDAO dao = getAelHorarioRotinaColetasDAO();
		dao.merge(horarioRotina);
		dao.flush();
		dao.desatachar(horarioRotina);
		
		if(!horarioRotina.getId().getUnfSeq().equals(horarioRotina.getIdAux().getUnfSeq())
		|| !horarioRotina.getId().getUnfSeqSolicitante().getSeq().equals(horarioRotina.getIdAux().getUnfSeqSolicitante().getSeq())
		|| !horarioRotina.getId().getDia().equals(horarioRotina.getIdAux().getDia())
		|| !horarioRotina.getId().getHorario().equals(horarioRotina.getIdAux().getHorario())){
			/*Agora excluo*/
			AelHorarioRotinaColetas horRotExc = dao.obterAelHorarioRotinaColetaPorId(horarioRotina.getIdAux());
			dao.remover(horRotExc);
		}
	}

	/**
	 * ORADB AELT_HRC_BRI - aelk_ael_rn.rn_aelp_atu_servidor
	 * @throws ApplicationBusinessException  
	 */
	protected void preInsert(AelHorarioRotinaColetas horarioRotina) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		
		AelHorarioRotinaColetas horRot = getAelHorarioRotinaColetasDAO().obterAelHorarioRotinaColetaPorId(horarioRotina.getId());
		if(horRot!=null){
			throw new ApplicationBusinessException(ManterHorarioColetaExceptionCode.MENSAGEM_HORARIO_EXISTENTE);
		}
		
		//Atribui ao exame a data de criação do horário como o dia corrente
		horarioRotina.setCriadoEm(new Date());
		//Seta ser_matricula e ser_vin_codigo 
		horarioRotina.setServidor(servidorLogado);
	}

	/**
	 * ORADB AELT_HRC_BRU - aelk_ael_rn.rn_aelp_atu_servidor
	 * @throws ApplicationBusinessException  
	 */
	protected void preUpdate(AelHorarioRotinaColetas horarioRotina) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//Seta ser_matricula e ser_vin_codigo
		horarioRotina.setAlteradoEm(new Date());
		horarioRotina.setServidorAlterado(servidorLogado);
	}

	/**
	 * ORADB TRIGGER AELT_HRC_ASD - aelp_enforce_hrc_rules
	 * isUpdate = true para update, false para delete
	 * @throws ApplicationBusinessException 
	 */
	protected void verificaUltimoHorarioUnidade(AelHorarioRotinaColetas horarioRotina, boolean isUpdate) throws ApplicationBusinessException {
		if(horarioRotina.getIndSituacao().equals(DominioSituacao.I) || !isUpdate){
			List<AelPermissaoUnidSolic> permSolic = getAelPermissaoUnidSolicDAO().buscarAelPermissaoUnidSolicPorUnfSeqSolicitanteSimNaoRotina(horarioRotina.getId().getUnfSeqSolicitante().getSeq(), DominioSimNaoRotina.R);
	
			if(permSolic!=null && permSolic.size() > 0){
				List<AelHorarioRotinaColetas> listaHorarios  = getAelHorarioRotinaColetasDAO().obterAelHorarioRotinaColetasPorParametros(horarioRotina.getId().getUnfSeq(), horarioRotina.getId().getUnfSeqSolicitante().getSeq(), null, null, DominioSituacao.A);
				if(listaHorarios!=null && listaHorarios.size()==1){
					if(isUpdate){
						throw new ApplicationBusinessException(ManterHorarioColetaExceptionCode.AEL_01344);
					}else{
						throw new ApplicationBusinessException(ManterHorarioColetaExceptionCode.AEL_01343);
					}
				}
			}
		}
	}
	
	protected AelHorarioRotinaColetasDAO getAelHorarioRotinaColetasDAO() {
		return aelHorarioRotinaColetasDAO;
	}
	
	protected AelPermissaoUnidSolicDAO getAelPermissaoUnidSolicDAO(){
		return aelPermissaoUnidSolicDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
