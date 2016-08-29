package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacUnidFuncionalSalasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AacUnidFuncionalSalasId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas
 * com o Zonas/Salas.
 * 
 */
@Stateless
public class ZonaSalaON extends BaseBusiness {


@EJB
private ZonaSalaRN zonaSalaRN;

private static final Log LOG = LogFactory.getLog(ZonaSalaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IExamesFacade examesFacade;

@Inject
private AacUnidFuncionalSalasDAO aacUnidFuncionalSalasDAO;

@EJB
private IAdministracaoFacade administracaoFacade;

@Inject
private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1558128270405728467L;

	public enum ZonaSalaONExceptionCode implements BusinessExceptionCode {
		LABEL_UNIDADE_SALA_JA_CADASTRADA,
		ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_MPM_ALTA_PEDIDO_EXAMES, //N\u00E3o \u00E9 poss\u00EDvel excluir unidade funcional por sala enquanto existir dependentes em MPM_ALTA_PEDIDO_EXAMES
		ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AGH_MICROCOMPUTADORES,  //N\u00E3o \u00E9 poss\u00EDvel excluir unidade funcional por sala enquanto existir dependentes em AGH_MICROCOMPUTADORES
		ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AEL_PEDIDOS_EXAMES, 	 //N\u00E3o \u00E9 poss\u00EDvel excluir unidade funcional por sala enquanto existir dependentes em AEL_PEDIDOS_EXAMES
		ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AAC_GRADE_AGENDAMEN_CONSULTAS; //N\u00E3o \u00E9 poss\u00EDvel excluir unidade funcional por sala enquanto existir dependentes em GRADE_AGENDAMEN_CONSULTAS
	}
	

	/** Metodo para persistir zona/sala
	 * 
	 * @param oldZonaSala
	 * @param newZonaSala
	 * @param operacao
	 * @throws BaseException
	 */
	public void persistirZonaSala(AacUnidFuncionalSalas oldZonaSala, AacUnidFuncionalSalas newZonaSala, 
			DominioOperacoesJournal operacao) throws BaseException {
		
		//AacUnidFuncionalSalasDAO dao = getAacUnidFuncionalSalasDAO();		
		if (operacao.equals(DominioOperacoesJournal.INS)) {
			preInsert(newZonaSala);
			getZonaSalaRN().zonaSalaAntesInserir(newZonaSala);
			aacUnidFuncionalSalasDAO.persistir(newZonaSala);
		}
		else { //update
			getZonaSalaRN().zonaSalaAntesAtualizar(oldZonaSala, newZonaSala);
			aacUnidFuncionalSalasDAO.atualizar(newZonaSala);
		}
		
	}
	
	/** Metodo para remover zona/sala, a verificação de dependencia é feita via consulta
	 *  ao invés de verificar se a constraint foi violada ao excluir.
	 *  Essa foi uma recomendação da arquitetura, pois em outros hospitais as foreign keys
	 *  poderão ter nomes diferentes
	 * 
	 * @param unfSeq
	 * @param sala
	 * @throws BaseException
	 */
	public void removerZonaSala(Short unfSeq, Byte sala) throws BaseException {
		AacUnidFuncionalSalasDAO dao = getAacUnidFuncionalSalasDAO();
		//Verifica se tem dependencia por select (não testa constraint violada) => arquitetura recomendou fazer assim
		verificaDependendia(unfSeq, sala);
		dao.remover(dao.obterUnidFuncionalSalasPeloId(new AacUnidFuncionalSalasId(unfSeq, sala)));
		dao.flush();
		/*} 
		catch (PersistenceException e) {
			logInfo("Erro ao remover a unidade funcional.", e);
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				
				if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause()).getConstraintName(),"MPM_AEX_UNF_FK1")) {
					throw new ApplicationBusinessException(//possui dependencia em MPM_ALTA_PEDIDO_EXAMES
							ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_MPM_ALTA_PEDIDO_EXAMES);
				
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
						.getConstraintName(),"AGH_MIC_UNF_FK1")) {
					throw new ApplicationBusinessException(//se possui dependencia em AGH_MICROCOMPUTADORES
							ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AGH_MICROCOMPUTADORES);
					
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
					.getConstraintName(),"AEL_PEX_USL_FK1")) {
					throw new ApplicationBusinessException(//se possui dependencia em AEL_PEDIDOS_EXAMES
							ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AEL_PEDIDOS_EXAMES);
				
				} else if (StringUtils.containsIgnoreCase(((ConstraintViolationException) e.getCause())
					.getConstraintName(),"AAC_GRD_USL_FK1")) {
					throw new ApplicationBusinessException(//se possui dependencia em AEL_PEDIDOS_EXAMES
							ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AAC_GRADE_AGENDAMEN_CONSULTAS);
				}				
				
			}
				
		}*/
	}
	
	/** Verifica dependência antes de excluir
	 * 
	 * @param unfSeq
	 * @param sala
	 * @throws BaseException
	 */
	protected void verificaDependendia(Short unfSeq, Byte sala) throws BaseException {
		
		String labelSala = this.obterParametroSala();
				
		if (!getPrescricaoMedicaFacade().pesquisarMpmAltaPedidoExamePorZonaESala(unfSeq, sala).isEmpty()){
			throw new ApplicationBusinessException(//possui dependencia em MPM_ALTA_PEDIDO_EXAMES
					ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_MPM_ALTA_PEDIDO_EXAMES, labelSala);
		}
		if (!getAdministracaoFacade().pesquisarAghMicrocomputadorPorSala(sala).isEmpty()){
			throw new ApplicationBusinessException(//possui dependencia em AGH_MICROCOMPUTADORES
					ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AGH_MICROCOMPUTADORES, labelSala);
		}
		if (!getAdministracaoFacade().pesquisarAghMicrocomputadorPorZonaESala(unfSeq, sala).isEmpty()){
			throw new ApplicationBusinessException(//possui dependencia em AGH_MICROCOMPUTADORES
					ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AGH_MICROCOMPUTADORES);
		}
		if (!getExamesFacade().pesquisarAelPedidoExamePorZonaESala(unfSeq,
				sala).isEmpty()) {
			throw new ApplicationBusinessException(
					// possui dependencia em MPM_ALTA_PEDIDO_EXAMES
					ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AEL_PEDIDOS_EXAMES,
					labelSala);
		}		
		if (!getAacGradeAgendamenConsultasDAO()
				.pesquisarAacGradeAgendamenConsultasPorZonaESala(unfSeq, sala)
				.isEmpty()) {
			throw new ApplicationBusinessException(
					// possui dependencia em GRADE_AGENDAMEN_CONSULTAS
					ZonaSalaONExceptionCode.ERRO_REMOVER_UNIDADE_SALA_CONSTRAINT_AAC_GRADE_AGENDAMEN_CONSULTAS,
					labelSala);
		}
		}

	protected void preInsert(AacUnidFuncionalSalas zonaSala) throws BaseException {
		String labelSala = this.obterParametroSala();
		
		if (getAacUnidFuncionalSalasDAO().obterUnidFuncionalSalasPeloId(zonaSala.getId()) != null){
			throw new ApplicationBusinessException(
					ZonaSalaONExceptionCode.LABEL_UNIDADE_SALA_JA_CADASTRADA, labelSala);	
		}
	}
	
	protected String obterParametroSala() {
		String labelSala;
		try {
			labelSala = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			labelSala = "Sala";
		}
		
		return labelSala;
	}
	
	protected AacUnidFuncionalSalasDAO getAacUnidFuncionalSalasDAO() {
		return aacUnidFuncionalSalasDAO;
	}
	
	protected ZonaSalaRN getZonaSalaRN() {
		return zonaSalaRN;
	}
	
	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}	
}
