package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.GenericJDBCException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaGrade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FinalizaAtendimentoON extends BaseBusiness {
	
	@EJB
	private ProcedimentoAtendimentoConsultaRN procedimentoAtendimentoConsultaRN;
	
	@EJB
	private FinalizaAtendimentoRN finalizaAtendimentoRN;
	
	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private AtendimentoPacientesAgendadosON atendimentoPacientesAgendadosON;

	private static final Log LOG = LogFactory.getLog(FinalizaAtendimentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6526732974476779470L;

	public enum FinalizaAtendimentoONExceptionCode implements BusinessExceptionCode {
		CONCLUSAO_NAO_EFETUADA_1, CONCLUSAO_NAO_EFETUADA_2,
		CONCLUSAO_NAO_EFETUADA_3, MAM_02318;
	}
	
	private static final String ANAMNESE="A", EVOLUCAO="E";
	
	//Trechos comentados conforme melhoria #11601
	public Boolean concluirBlocoNaoAssina(AacConsultas consulta, String tipoCorrente) throws ApplicationBusinessException {
		
		consulta = getAacConsultasDAO().obterPorChavePrimaria(consulta.getNumero());
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Short vinCodigo = null;
		Integer matricula = null;
		if(servidorLogado!=null && servidorLogado.getId()!=null){
			vinCodigo = servidorLogado.getId().getVinCodigo();
			matricula = servidorLogado.getId().getMatricula();
		}
		
		if(ANAMNESE.equals(tipoCorrente)){
			Short codigoProcessoAnamnese = this.getAtendimentoPacientesAgendadosON().retornarCodigoProcessoAnamnese();
			if(!this.getAtendimentoPacientesAgendadosON().validarProcesso(vinCodigo, matricula, codigoProcessoAnamnese)){
				//throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.MAM_00728);
				return false;
			}
		}
		
		if (EVOLUCAO.equals(tipoCorrente)){
			Short codigoProcessoEvolucao = this.getAtendimentoPacientesAgendadosON().retornarCodigoProcessoEvolucao();
			if(!this.getAtendimentoPacientesAgendadosON().validarProcesso(vinCodigo, matricula, codigoProcessoEvolucao)){
				//throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.MAM_00729);
				return false;
			}
		}
		
		if (consulta.getReceituarios()!=null && !consulta.getReceituarios().isEmpty()){
			Short codigoProcessoReceita = this.getAtendimentoPacientesAgendadosON().retornarCodigoProcessoReceita();
			if(!this.getAtendimentoPacientesAgendadosON().validarProcesso(vinCodigo, matricula, codigoProcessoReceita)){
				//throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.MAM_01177);
				return false;
			}
		}
		
		Short codigoProcessoRelatorio = this.getAtendimentoPacientesAgendadosON().retornarCodigoProcessoRelatorio();
		if(!this.getAtendimentoPacientesAgendadosON().validarProcesso(vinCodigo, matricula, codigoProcessoRelatorio)){
			//throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.MAM_01179);
			return false;
		}

		if (getExamesFacade().buscarSolicitacaoExamesPorAtendimento(consulta.getAtendimento()) != null && !getExamesFacade().buscarSolicitacaoExamesPorAtendimento(consulta.getAtendimento()).isEmpty()){
			Short codigoProcessoExame = this.getAtendimentoPacientesAgendadosON().retornarCodigoProcessoExame();
			if(!this.getAtendimentoPacientesAgendadosON().validarProcesso(vinCodigo, matricula, codigoProcessoExame)){
				//throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.MAM_01202);
				return false;
			}
		}
		return true;
	}
	//Método criado para finalizar atendimento pelo Aba 1 das consultas do Ambulatório
	public void finalizarAtendimento(Integer consultaNumero,String nomeMicrocomputador) throws BaseException,GenericJDBCException{
		this.getFinalizarAtendimentoRN().concluirAtendimento(consultaNumero, new Date(), nomeMicrocomputador, new Date());
	}
	public void finalizarAtendimento(AacConsultas consulta, Boolean temProcedimentoRealizado, String nomeMicrocomputador) throws BaseException, GenericJDBCException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificarProcedimentos(consulta, temProcedimentoRealizado);
		this.verificarAnamneseEvolucao(consulta);
		this.verificarProcedimentoRealizado(consulta);
		Short vinCodigo = null;
		Integer matricula = null;
		if(servidorLogado!=null && servidorLogado.getId()!=null){
			vinCodigo = servidorLogado.getId().getVinCodigo();
			matricula = servidorLogado.getId().getMatricula();
		}
		this.getFinalizarAtendimentoRN().concluirAtendimento(consulta, new Date(), vinCodigo, matricula, nomeMicrocomputador, new Date());
	}
	
	public void verificarProcedimentos(AacConsultas consulta, Boolean temProcedimentoRealizado) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Integer grdSeq = null;
		if(consulta.getGradeAgendamenConsulta()!=null){
			grdSeq = consulta.getGradeAgendamenConsulta().getSeq(); 
		}
		if(!this.getAmbulatorioConsultaRN().verificarCaracteristicaGrade(grdSeq, DominioCaracteristicaGrade.AGENDA_PRESCRICAO_QUIMIO)){
			if(!temProcedimentoRealizado){
				throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.CONCLUSAO_NAO_EFETUADA_3);
				
			}
		}
		
		if(this.getAmbulatorioConsultaRN().verificarCaracteristicaGrade(grdSeq, DominioCaracteristicaGrade.AGENDA_PRESCRICAO_QUIMIO)){
			AghParametros parametroNenhumProcedimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SEQ_PROC_NENHUM);
			Integer prdSeq = parametroNenhumProcedimento.getVlrNumerico().intValue();
			MamProcedimentoRealizado procedimentoRealizado = new MamProcedimentoRealizado();
			procedimentoRealizado.setDthrCriacao(new Date());
			procedimentoRealizado.setSituacao(DominioSituacao.A);
			MamProcedimento procedimento = this.getMamProcedimentoDAO().obterPorChavePrimaria(prdSeq);
			procedimentoRealizado.setProcedimento(procedimento);
			procedimentoRealizado.setConsulta(consulta);
			procedimentoRealizado.setPendente(DominioIndPendenteAmbulatorio.P);
			procedimentoRealizado.setPadraoConsulta(false);
			procedimentoRealizado.setServidor(servidorLogado);
			try{
				this.getProcedimentoAtendimentoConsultaRN().inserirProcedimentoRealizado(procedimentoRealizado, false);	
			} catch(Exception e){
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.MAM_02318);
			}
			
		}
	}
	
	public void verificarAnamneseEvolucao(AacConsultas consultaSelecionada) throws ApplicationBusinessException{
		
		MamAnamneses anamnese = getMamAnamnesesDAO().obterAnamneseAtivaPorNumeroConsulta(consultaSelecionada.getNumero());
		MamEvolucoes evolucao = getMamEvolucoesDAO().obterEvolucaoAtivaPorNumeroConsulta(consultaSelecionada.getNumero());
		
		Boolean finalizaSemEvolucao = verificarAtendimentoTecnicoEnfermagem(consultaSelecionada);
		
		if (!finalizaSemEvolucao){
			if(evolucao == null){
				evolucao = this.mamEvolucoesDAO.obterEvolucaoQuestionarioAtivaPorNumeroConsulta(consultaSelecionada.getNumero());
			}
			if (anamnese==null && evolucao==null){
				throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.CONCLUSAO_NAO_EFETUADA_1);
			}
		}
		if (anamnese!=null && evolucao!=null){
			throw new ApplicationBusinessException(FinalizaAtendimentoONExceptionCode.CONCLUSAO_NAO_EFETUADA_2);	
		}
	}
	
	
	private Boolean verificarAtendimentoTecnicoEnfermagem(AacConsultas consulta) throws ApplicationBusinessException{
		Boolean retorno = false;
		AacGradeAgendamenConsultas grade = consulta.getGradeAgendamenConsulta();
		if (grade.getProcedimento()){
			if (cascaFacade.usuarioTemPerfil(this.obterLoginUsuarioLogado(), "ENF05.1")){
				retorno = true;
			}
		}
		return retorno;
	}
	
	/**
	 * Procedure
	 * ORADB P_VERIFICA_PROCED_CID
	 * @param consultaSelecionada
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void verificarProcedimentoRealizado(AacConsultas consultaSelecionada) throws ApplicationBusinessException{
		List<MamProcedimentoRealizado> listaProcedimentoRealizado = this.getMamProcedimentoRealizadoDAO().pesquisarProcedimentoRealizadoPendentePorNumeroConsulta(consultaSelecionada.getNumero());
		for(MamProcedimentoRealizado procedimentoRealizado: listaProcedimentoRealizado){
			Integer prdSeq = null;
			if(procedimentoRealizado!=null && procedimentoRealizado.getProcedimento()!=null){
				prdSeq = procedimentoRealizado.getProcedimento().getSeq();
			}
			Integer cidSeq = null;
			if(procedimentoRealizado!=null && procedimentoRealizado.getCid()!=null){
				cidSeq = procedimentoRealizado.getCid().getSeq();
			}
			if(procedimentoRealizado!=null && procedimentoRealizado.getCid()==null){
				this.getAtendimentoPacientesAgendadosON().verificarCidProcedimento(consultaSelecionada.getNumero(), prdSeq, cidSeq);
			}	
		}
	}

	protected AtendimentoPacientesAgendadosON getAtendimentoPacientesAgendadosON(){
		return atendimentoPacientesAgendadosON;
	}
	
	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN(){
		return ambulatorioConsultaRN;
	}
	
	protected ProcedimentoAtendimentoConsultaRN getProcedimentoAtendimentoConsultaRN(){
		return procedimentoAtendimentoConsultaRN;
	}
	
	protected FinalizaAtendimentoRN getFinalizarAtendimentoRN(){
		return finalizaAtendimentoRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}
	
	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}
	
	protected MamAnamnesesDAO getMamAnamnesesDAO(){
		return mamAnamnesesDAO;
	}
	
	protected MamEvolucoesDAO getMamEvolucoesDAO(){
		return mamEvolucoesDAO;
	}
	
	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected IExamesFacade getExamesFacade(){
		return this.examesFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
