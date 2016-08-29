package br.gov.mec.aghu.ambulatorio.business;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacAtendimentoApacsDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacProcedHospEspecialidadesDAO;
import br.gov.mec.aghu.ambulatorio.dao.FatLaudosImpressaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.FatLaudosPacApacsDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.ambulatorio.vo.QuantidadeAparelhoAuditivoVO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaGrade;
import br.gov.mec.aghu.dominio.DominioModuloFatContaApac;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSinalizacaoControleFrequencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.StatusPacienteAgendado;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatPacienteTransplantesDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatResumoApacsDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.faturamento.vo.CaracteristicaPhiVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadosVO;
import br.gov.mec.aghu.faturamento.vo.TipoProcedHospitalarInternoVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AacProcedHospEspecialidadesId;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.FatLaudoPacApac;
import br.gov.mec.aghu.model.FatLaudosImpressao;
import br.gov.mec.aghu.model.FatListaPacApac;
import br.gov.mec.aghu.model.FatPacienteTransplantes;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatResumoApacs;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.parametrosistema.dao.AghParametrosDAO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;



@Stateless
public class ConsultasON extends BaseBusiness{
	@EJB
	private ConsultasRN consultasRN;
	private static final Log LOG = LogFactory.getLog(ConsultasON.class);
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	@Inject
	private AacProcedHospEspecialidadesDAO aacProcedHospEspecialidadesDAO;
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private FatContaApacDAO fatContaApacDAO;
	
	@Inject
	private MarcacaoConsultaON marcacaoConsultaON;
	@Inject
	private FatPacienteTransplantesDAO fatPacienteTransplantesDAO;
	@Inject
	private AacAtendimentoApacsDAO aacAtendimentoApacsDAO;
	@Inject
	private AghParametrosDAO aghParametrosDAO;
	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;
	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;
	@Inject
	private FatLaudosImpressaoDAO fatLaudosImpressaoDAO;
	@Inject
	private FatResumoApacsDAO fatResumoApacsDAO;
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;
	@Inject
	private FatLaudosPacApacsDAO fatLaudosPacApacsDAO;
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;
	private static final long serialVersionUID = 9145047254128769146L;
//	private FatResumoApacs rResumo;
	private List<MbcCirurgias> rBuscaImplante;
	private FatProcedAmbRealizadosVO rPmr;
	private Integer phiDiag = null;
	private Date dtAnterior = null;
	private Map<AghuParametrosEnum, Object> parametrosProcedure = new HashMap<AghuParametrosEnum, Object>(18);
	
	private DominioSinalizacaoControleFrequencia returnVerificaLaudoOtorrino;
	private FatProcedAmbRealizadosVO rAparelhos;
	private enum ConsultaONExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_CONSULTA, ERRO_PROC_HOSP_ESPECIALIDADE_JA_EXISTE
	}
	/**
	 * Metodo para clonar uma entidade da classe AacConsultas 
	 * @param AacConsultas
	 * @return AacConsultas clonado.
	 * @throws ApplicationBusinessException 

	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public AacConsultas clonarConsulta(AacConsultas consulta) throws ApplicationBusinessException{
		AacConsultas cloneConsulta = null;
		try{
			cloneConsulta = (AacConsultas) BeanUtils.cloneBean(consulta);
		} catch(Exception e){
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ConsultaONExceptionCode.ERRO_CLONE_CONSULTA);
		}
		cloneConsulta.setConsulta(consulta.getConsulta()); 
		cloneConsulta.setGradeAgendamenConsulta(consulta.getGradeAgendamenConsulta()); 
		cloneConsulta.setConvenioSaudePlano(consulta.getConvenioSaudePlano()); 
		cloneConsulta.setServidor(consulta.getServidor()); 
		cloneConsulta.setSituacaoConsulta(consulta.getSituacaoConsulta()); 
		cloneConsulta.setServidorConsultado(consulta.getServidorConsultado()); 
		cloneConsulta.setServidorAlterado(consulta.getServidorAlterado()); 
		cloneConsulta.setAtendimento(consulta.getAtendimento()); 
		cloneConsulta.setRetorno(consulta.getRetorno()); 
		cloneConsulta.setMotivo(consulta.getMotivo()); 
		cloneConsulta.setCondicaoAtendimento(consulta.getCondicaoAtendimento()); 
		cloneConsulta.setTipoAgendamento(consulta.getTipoAgendamento()); 
		cloneConsulta.setPagador(consulta.getPagador()); 
		cloneConsulta.setSituacaoConsulta(consulta.getSituacaoConsulta()); 
		cloneConsulta.setServidorAtendido(consulta.getServidorAtendido()); 
		cloneConsulta.setProjetoPesquisa(consulta.getProjetoPesquisa()); 
		cloneConsulta.setFormaAgendamento(consulta.getFormaAgendamento()); 
		cloneConsulta.setControle(consulta.getControle()); 
		cloneConsulta.setAnamneses(consulta.getAnamneses()); 
		cloneConsulta.setServidorMarcacao(consulta.getServidorMarcacao()); 
		return cloneConsulta;
	}
	public void excluirProcedimentoEspecialidade(Short espSeq, Integer phiSeq) {
		AacProcedHospEspecialidadesId id = new AacProcedHospEspecialidadesId(espSeq, phiSeq);
		AacProcedHospEspecialidades procEsp = getAacProcedHospEspecialidadesDAO().obterPorChavePrimaria(id);
		getAacProcedHospEspecialidadesDAO().remover(procEsp);
		getAacProcedHospEspecialidadesDAO().flush();
	}
	public void persistirProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp, DominioOperacoesJournal operacao) throws BaseException {
		if(DominioOperacoesJournal.INS.equals(operacao)) {
			verificarProcedHospEspecialidadeExiste(procEsp);
			getConsultasRN().inserirProcedimentoEspecialidade(procEsp);
		}
		else if(DominioOperacoesJournal.UPD.equals(operacao)) {
			getConsultasRN().atualizarProcedimentoEspecialidade(procEsp);
		}
	}
	protected void verificarProcedHospEspecialidadeExiste(AacProcedHospEspecialidades procEsp) 
			throws ApplicationBusinessException {
		AacProcedHospEspecialidades procEspExistente = 
				getAacProcedHospEspecialidadesDAO().obterPorChavePrimaria(procEsp.getId());
		if (procEspExistente != null) {
			throw new ApplicationBusinessException(
					ConsultaONExceptionCode.ERRO_PROC_HOSP_ESPECIALIDADE_JA_EXISTE, 
					procEsp.getProcedHospInterno().getDescricao(),
					procEsp.getEspecialidade().getNomeEspecialidade());
		}
	}
	protected AacConsultasDAO getAacConsultasDAO(){
		return aacConsultasDAO;
	}
	
	/**
	 * Retorna o DAO de consultas
	 * 
	 * @return
	 */
	protected AacConsultasDAO getConsultasDAO() {
		return aacConsultasDAO;
	}
	protected ConsultasRN getConsultasRN() {
		return consultasRN;
	}
	protected AacProcedHospEspecialidadesDAO getAacProcedHospEspecialidadesDAO() {
		return aacProcedHospEspecialidadesDAO;
	}
	public AacConsultas obterPorChavePrimaria(Integer numero) {
		return getConsultasDAO().obterPorChavePrimaria(numero);
	}
	//Condicao comentada para atender a nova necessidade da estória #43295 - 15/12/2014
	public List<ConsultaAmbulatorioVO> consultaPacientesAgendados(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas, VAacSiglaUnfSala zonaSala,
			DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO,
			AghEspecialidades especialidade, RapServidores profissional,
			StatusPacienteAgendado status)
			throws ApplicationBusinessException {

		List<Short> sitPend = new ArrayList<Short>();
		List<ConsultaAmbulatorioVO> result = getAacConsultasDAO()
				.consultaPacientesAgendados(firstResult, maxResult, orderProperty, asc, 
						dtPesquisa, zonaUnfSeq, zonaSalas,
						zonaSala, turno, equipe, espCrmVO, especialidade,
						profissional, status, sitPend);

		Short parametroOtorrino = parametroFacade.buscarValorShort(AghuParametrosEnum.P_ESP_GENER_OTO);
		
		Map<Short, Short> mapEspecialidades = new HashMap<Short, Short>();
		preencherParametros();
		if ((status.equals(StatusPacienteAgendado.EM_ATENDIMENTO) || status.equals(StatusPacienteAgendado.ATENDIDO))
				&& result != null	&& !result.isEmpty()) {
			return this.verificarDocumentoPendenteCertificacaoDigital(result);
		} else if (status.equals(StatusPacienteAgendado.AGENDADO)) {
			for (ConsultaAmbulatorioVO item : result) {
				// P1 #42011
				if (item != null && item.getPacienteCodigo() != null && item.getPgdSeq() != null && item.getGradeEspSeq() != null) {
					DominioSinalizacaoControleFrequencia controleFrequencia = verificaLaudo(item, parametroOtorrino, mapEspecialidades);
					item.setControleFrequencia(controleFrequencia);
					
					if (controleFrequencia == null) {	
						sinalizarControleFrequenciaOtorrino(item);						
						if("N".equals(item.getIndicaImagem())){
							sinalizarControleFrequenciaOFTALMO(item);	
						}
					} else { 							
						if (controleFrequencia == DominioSinalizacaoControleFrequencia.TR) {
							laudosImpressoesGeral(item, "T");
						} else if (controleFrequencia == DominioSinalizacaoControleFrequencia.PL) {
							laudosImpressoesGeral(item, "T");
						} else if (controleFrequencia == DominioSinalizacaoControleFrequencia.PI) {
							laudosImpressoesGeral(item, "P");
						} else if (controleFrequencia == DominioSinalizacaoControleFrequencia.CF || controleFrequencia == DominioSinalizacaoControleFrequencia.DI ||
							controleFrequencia == DominioSinalizacaoControleFrequencia.AD || controleFrequencia == DominioSinalizacaoControleFrequencia.RE || 
							controleFrequencia == DominioSinalizacaoControleFrequencia.IC) {
							laudosImpressoesGeral(item, "O");			
							setMessage(item);
						}else if(controleFrequencia.equals(DominioSinalizacaoControleFrequencia.AP)){
							laudosImpressoesGeral(item, "O");
							setMessage(item);
						} else {
							item.setIndicaImagem("N");
						}
					}
				}
			}
			return result;
		} else {
			return result;
		}
	}
	public Long consultaPacientesAgendadosCount(Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas, VAacSiglaUnfSala zonaSala,
	DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO,
	AghEspecialidades especialidade, RapServidores profissional,
	StatusPacienteAgendado status){		
	List<Short> sitPend = new ArrayList<Short>();
	return this.getAacConsultasDAO()
				.consultaPacientesAgendadosCount(dtPesquisa, zonaUnfSeq, zonaSalas, zonaSala, turno, equipe, espCrmVO,
						especialidade, profissional, status, sitPend);
	}
	//Implementacao necessario para parametro tipoSinalizacao para mesma situacao de laudo
	private void laudosImpressoesGeral(ConsultaAmbulatorioVO item, String tipoSinalizacao) {
		List<FatLaudosImpressao> laudosImpressoes;
		laudosImpressoes = fatLaudosImpressaoDAO.listarLaudos(item.getPacienteCodigo(), tipoSinalizacao, item.getNumero());
		if (laudosImpressoes != null && laudosImpressoes.size() > 0) {
			item.setIndicaImagem("I");
		} else {
			item.setIndicaImagem("C");
		}
		item.setTipoSinalizacao(tipoSinalizacao);
	}	
		
	//sinalizaçao (verde) das consultas  - otorrino #42010 melhoria #44508
	public void sinalizarControleFrequenciaOtorrino(ConsultaAmbulatorioVO item)throws ApplicationBusinessException{
		List<FatLaudosImpressao> laudosImpressoes = null;
		//C1
		laudosImpressoes = fatLaudosImpressaoDAO.listarLaudos(item.getPacienteCodigo(), "O", item.getNumero());
		//C4 #44508
		List<AacConsultas> consulta = aacConsultasDAO.obterSinalizacaoDiagnostico(item.getNumero());	
		if(!consulta.isEmpty()){
			for(AacConsultas consultasI : consulta){
				if(marcacaoConsultaON.verificarCaracEspecialidade(consultasI.getGradeAgendamenConsulta().getEspecialidade(), DominioCaracEspecialidade.CAND_APAC_OTORRINO)){
					 if(laudosImpressoes != null && laudosImpressoes.isEmpty()){
						item.setIndicaImagem("C");
						item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE_LAUDO_DIAGNOSTICO_AUDITIVA"));
					 }else{
						item.setIndicaImagem("I");
						item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE_LAUDO_DIAGNOSTICO_AUDITIVA"));
					 }
				}else{
					item.setIndicaImagem("N");
				}
				item.setTipoSinalizacao("O");
			}
		}else{
			item.setIndicaImagem("N");
		}		
	}
	
	// sinalização (azul) das consultas - subespecialidade de OLS #42012
	public void sinalizarControleFrequenciaOFTALMO(ConsultaAmbulatorioVO item) throws ApplicationBusinessException{
		List<FatLaudosImpressao> laudosImpressoes = null;
		List<AacConsultas> consultaSinalizacaoLaudoFOTO = null;
		List<AacConsultas> consultaSinalizacaoControleFrequenciaFOTO;
		// C1
		laudosImpressoes = fatLaudosImpressaoDAO.listarLaudos(item.getPacienteCodigo(), "F", item.getNumero());
		AghEspecialidades especialidades = new AghEspecialidades(item.getGradeEspSeq());
		Boolean retornoEspecialidade = getMarcacaoConsultaON().verificarCaracEspecialidade(especialidades,  DominioCaracEspecialidade.CAND_APAC_FOTO);
		Integer parametroOftalmo = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_TRAT_OFTALMO);
		if (retornoEspecialidade) {
			//C3
			consultaSinalizacaoLaudoFOTO = aacConsultasDAO.verificarNecessidadeSinalizacaoLaudoFoto(item.getNumero(), parametroOftalmo);
			//C2		
			consultaSinalizacaoControleFrequenciaFOTO = aacConsultasDAO.verificarNecessidadeSinalizacaoControleFrequenciaFoto(item.getNumero(), parametroOftalmo);
		} else {
			consultaSinalizacaoLaudoFOTO = null;
			consultaSinalizacaoControleFrequenciaFOTO = null;
		}
		//circulo azul
		if((laudosImpressoes.isEmpty() && (consultaSinalizacaoLaudoFOTO != null && !consultaSinalizacaoLaudoFOTO.isEmpty())) 
				|| (laudosImpressoes.isEmpty() && (consultaSinalizacaoControleFrequenciaFOTO != null && !consultaSinalizacaoControleFrequenciaFOTO.isEmpty()))){
			item.setIndicaImagem("C");
			item.setTipoSinalizacao("F");
		//impressora azul
		} else if (!laudosImpressoes.isEmpty() && (consultaSinalizacaoLaudoFOTO != null && !consultaSinalizacaoLaudoFOTO.isEmpty())){
			item.setIndicaImagem("I");
			item.setTipoSinalizacao("F");
		//vazio 
		} else if (((consultaSinalizacaoLaudoFOTO != null && !consultaSinalizacaoLaudoFOTO.isEmpty()) && (consultaSinalizacaoControleFrequenciaFOTO != null && !consultaSinalizacaoControleFrequenciaFOTO.isEmpty())) 
				|| (!laudosImpressoes.isEmpty() && (consultaSinalizacaoLaudoFOTO != null && !consultaSinalizacaoLaudoFOTO.isEmpty()))){
			item.setIndicaImagem("N");
		}
	}
	
	private void setMessage(ConsultaAmbulatorioVO item) {
		if (item.getControleFrequencia() == DominioSinalizacaoControleFrequencia.DI){
			if (item.getIndicaImagem().equalsIgnoreCase("C")) {
				item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE_LAUDO_DIAGNOSTICO_AUDITIVA"));
			} else if (item.getIndicaImagem().equalsIgnoreCase("I")) {
				item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE_LAUDO_DIAGNOSTICO_AUDITIVA"));
			}
		}else if(item.getControleFrequencia() == DominioSinalizacaoControleFrequencia.RE) {
			if (item.getIndicaImagem().equalsIgnoreCase("C")) {
				item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE_LAUDO_REAVALIACAO_DIAGNOSTICA_AUDITIVA"));
			} else if (item.getIndicaImagem().equalsIgnoreCase("I")) {
				item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE_LAUDO_REAVALIACAO_DIAGNOSTICA_AUDITIVA"));
			}
		} else if (item.getControleFrequencia() == DominioSinalizacaoControleFrequencia.AD) {
			if (item.getIndicaImagem().equalsIgnoreCase("C")) {
				item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE_LAUDO_PACIENTE_ADAPTADO"));
			} else if (item.getIndicaImagem().equalsIgnoreCase("I")) {
				item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE_LAUDO_PACIENTE_ADAPTADO"));
			}
		} else if (item.getControleFrequencia() == DominioSinalizacaoControleFrequencia.IC) {
			if (item.getIndicaImagem().equalsIgnoreCase("C")) {
				item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE_LAUDO_PACIENTE_IMPLANTE_COCLEAR"));
			} else if (item.getIndicaImagem().equalsIgnoreCase("I")) {
				item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE_LAUDO_PACIENTE_IMPLANTE_COCLEAR"));
			}
		} else if (item.getControleFrequencia() == DominioSinalizacaoControleFrequencia.CF) {
			if (item.getIndicaImagem().equalsIgnoreCase("C")) {
				item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE"));
			} else if (item.getIndicaImagem().equalsIgnoreCase("I")) {
				item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE"));
			}
		} else if(item.getControleFrequencia() == DominioSinalizacaoControleFrequencia.AP){
			if (item.getIndicaImagem().equalsIgnoreCase("C")) {
				item.setTitle(this.getResourceBundleValue("TITLE_IMPRIMIR_CONTROLE_FREQUENCIA_LAUDO_APARELHO"));
			} else if (item.getIndicaImagem().equalsIgnoreCase("I")) {
				item.setTitle(this.getResourceBundleValue("TITLE_REIMPRIMIR_CONTROLE_FREQUENCIA_LAUDO_APARELHO"));
			}
		}

	}
	
	/**
	 * @ORADB: FUNCTION FATC_VER_CAP_OTO
	 * @author marcos.silva
	 * #42011
	 */
	private DominioSimNao verificaOtorrino (Integer pacCodigo, DominioModuloFatContaApac modulo) {
		Boolean consulta = fatContaApacDAO.existeContaApac(pacCodigo, modulo);
		if (!consulta) {
			return DominioSimNao.N;
		} else {
			return DominioSimNao.S;
		}
	}
	/**
	 * @ORADB: FUNCTION C_VERIFICA_LAUDO
	 * @author marcos.silva
	 * #42011
	 */
	public DominioSinalizacaoControleFrequencia verificaLaudo(
			ConsultaAmbulatorioVO consulta, Short parametroOtorrino, Map<Short, Short> mapEspecialidades) throws ApplicationBusinessException {
		DominioSinalizacaoControleFrequencia vLaudo = null;
		Integer consultaNumero = consulta.getNumero();
		Integer pacienteCodigo = consulta.getPacienteCodigo(); 
		String p_ind_sit_consulta = consulta.getSituacaoConsulta(); 
		Short p_fag_pgd_seq = consulta.getPgdSeq(); 
		Short espSeq = consulta.getGradeEspSeq();
		String verificaTransplanteLaudo = verificaTransplanteLaudo(pacienteCodigo, consultaNumero);
		Short seqEspecialidade = obterEspecialidadeSeq(mapEspecialidades, espSeq);
		if ("N".equalsIgnoreCase(verificaTransplanteLaudo)) {
			if ("M".equalsIgnoreCase(p_ind_sit_consulta)) {
				if (p_fag_pgd_seq == 1) {
					if (seqEspecialidade != null && seqEspecialidade.shortValue() == parametroOtorrino.shortValue()) { 
						if (verificaOtorrino(pacienteCodigo, DominioModuloFatContaApac.APAT) == DominioSimNao.S) {
							vLaudo = DominioSinalizacaoControleFrequencia.CF;
						} else {
							vLaudo = verificaLaudoOtorrino(consultaNumero, pacienteCodigo);
						}
					}
				}
			}
		} else if (verificaTransplanteLaudo.equalsIgnoreCase("S")) {
			vLaudo = DominioSinalizacaoControleFrequencia.TR;
		} else if (verificaTransplanteLaudo.equalsIgnoreCase("L")) {
			vLaudo = DominioSinalizacaoControleFrequencia.PL;
		}
		if (vLaudo == null) {
			if (pendenciaImpresao(consultaNumero)) {
				vLaudo = DominioSinalizacaoControleFrequencia.PI;
			}
		}
		return vLaudo;
	}
	private Short obterEspecialidadeSeq(Map<Short, Short> mapEspecialidades,
			Short espSeq) {
		Short seqEspecialidade;
		if (mapEspecialidades.get(espSeq) != null) {
			seqEspecialidade = mapEspecialidades.get(espSeq);
		} else {
			seqEspecialidade = aghEspecialidadesDAO.listarEspecialidades(espSeq);
			mapEspecialidades.put(espSeq, seqEspecialidade);
		}
		return seqEspecialidade;
	}
	
	/**
	 * @ORADB: PROCEDURE C_PENDENCIA_IMPR
	 * @author lucas.carvalho
	 * 42013
	 * @param consultaNumero
	 * @return
	 */
	private boolean pendenciaImpresao(Integer consultaNumero) {
		boolean vCtrlFreq;
		boolean vPendImpr;
		boolean vRetorno = false;
		vCtrlFreq = aacConsultasDAO.verificaConsultaGradeAgendamenEspecialidadeTratamentosPorConNumero(consultaNumero);
		if (vCtrlFreq) {
			vPendImpr = fatLaudosPacApacsDAO.verificaLaudoListaApacEspecialidadeTratamentoConsultaGradeAgendamen(consultaNumero);
			if (vPendImpr == false) {
				vRetorno = true;
			}
			if (vRetorno == false) {
				FatLaudoPacApac fatLaudosPacApac = fatLaudosPacApacsDAO.verificaPacienteRealizouTransplante(consultaNumero);
				if (fatLaudosPacApac != null) {
					vRetorno = true;
					FatListaPacApac listaPacApac = fatLaudosPacApac.getFatListaPacApac();
					listaPacApac.setIndSituacao(DominioSituacao.I);
					fatLaudosPacApacsDAO.persistir(listaPacApac);
				}
			}
		}
		return vRetorno;
	}
	
	/**
	 *@ORADB: FUNCTION FATC_VER_LAUDO_OTO
	 *@author marcos.silva
	 */
	//Verifica laudo Otorrino (sinalização [verde] das consultas)
	private DominioSinalizacaoControleFrequencia verificaLaudoOtorrino(Integer consultaNumero, Integer pacienteCodigo) throws ApplicationBusinessException {
		returnVerificaLaudoOtorrino = null;
		FatResumoApacs rResumo;
		Byte seqpContaApac = buscaConta(pacienteCodigo);
		if (seqpContaApac != null) {
			return null;
		}
		
		FatProcedAmbRealizadosVO fatProcedAmbRealizado = 
				fatProcedAmbRealizadoDAO.buscaFatProcedAmbRealizado(consultaNumero, 
						Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_TRATAMENTO_OTORRINO))));
		if (fatProcedAmbRealizado == null) {
			return null;
		} else {
			fatProcedAmbRealizado.setIndOts(marcacaoConsultaON.verificarCaracEspecialidade(new AghEspecialidades(fatProcedAmbRealizado.getSeqEspecialidade()), DominioCaracEspecialidade.CAND_APAC_OTORRINO));
			setrPmr(fatProcedAmbRealizado);
		}
			
		dtAnterior = DateUtil.obterUltimoDiaDoMes(DateUtil.adicionaDias(rPmr.getDthrRealizado(), -365));
		
		verificaIdadePaciente(rPmr.getPacCodigo(), (Date) parametrosProcedure.get(AghuParametrosEnum.P_DT_FIM_COMP_APT));

//		c_busca_resumo INTO r_resumo
		FatResumoApacs fatResumoApacs = fatResumoApacsDAO.buscaResumo(pacienteCodigo, null);
		rResumo = fatResumoApacs;
		if (fatResumoApacs == null) {
			if (fatProcedAmbRealizado.isIndOts()) {
				returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.DI; 
				return returnVerificaLaudoOtorrino;
			} else {
				return null;
			}
		} else {
			verificaAcompImplante(rResumo);
			if (returnVerificaLaudoOtorrino != null) {
				return returnVerificaLaudoOtorrino;
			} 
			// Inicio #44682
			boolean possuiCaracteristica = false;
			//Recupera consulta para poder obter a GradeAgendamentoConsulta
			AacConsultas consulta = aacConsultasDAO.obterConsulta(consultaNumero);
			
			if(consulta != null && consulta.getGradeAgendamenConsulta() != null && consulta.getGradeAgendamenConsulta().getSeq() != null){
				//Verfica se a consulta tem caracteristica adaptacao_protese
				possuiCaracteristica = ambulatorioConsultaRN.verificarCaracteristicaGrade(consulta.getGradeAgendamenConsulta().getSeq(), DominioCaracteristicaGrade.ADAPTACAO_PROTESE);
			}
						
			QuantidadeAparelhoAuditivoVO quantidadeAparelhoAuditivoVO = new QuantidadeAparelhoAuditivoVO();
			
			if(possuiCaracteristica){
				//Executa C3
				quantidadeAparelhoAuditivoVO = aghAtendimentoDAO.verificarQuantidadeAparelhosAuditivos(pacienteCodigo);
				if(quantidadeAparelhoAuditivoVO.getQuantidade() > 0){
					return DominioSinalizacaoControleFrequencia.AP;
				}
			}else{
				verificaApac(rResumo);
			}
			return returnVerificaLaudoOtorrino;
		}
	}
	
	//c_busca_conta INTO v_cap_seqp;
	private Byte buscaConta(Integer pacienteCodigo) {
		FatContaApac fatContaApac = fatContaApacDAO.buscaContaApac(pacienteCodigo);
		if (fatContaApac != null) {
			return fatContaApac.getId().getSeqp();
		}
		return null;
	}
	/** #42803
	 * @throws ApplicationBusinessException 
	 *@ORADB: PROCEDURE VER_IDADE_PAC
	 * Modificação da consulta 28/11/2014
	 * Obs: retorno de VO apenas para usabilidade maior
	 */
	public TipoProcedHospitalarInternoVO verificaIdadePaciente(Integer codigoPaciente, Date dataFim) throws ApplicationBusinessException {
		Integer idade = 0;
		if (parametrosProcedure == null) {
			preencherParametros();
		}
		idade = idadePaciente(codigoPaciente, dataFim);
		TipoProcedHospitalarInternoVO tipoProcedHospitalarInternoVO = new TipoProcedHospitalarInternoVO(); 
		if (idade != null) {
			tipoProcedHospitalarInternoVO.setPhiSelecao(Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_SELECAO))));
			tipoProcedHospitalarInternoVO.setPhiReaDiag(Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_REAV_DIAG_MAIOR_3ANOS))));
			tipoProcedHospitalarInternoVO.setPhiAdaptado(Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_ADULTO))));
			tipoProcedHospitalarInternoVO.setPhiAcomimpl(Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_ADULTO))));
			tipoProcedHospitalarInternoVO.setPhiTerapia(Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_TERAPIA_ADULTO))));
			
			if(idade < 3){
				phiDiag = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_DIAG_DEF_MENOR_3ANOS)));
				tipoProcedHospitalarInternoVO.setPhiDiag(phiDiag);
			}else{
				phiDiag = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_DIAG_DEF_MAIOR_3ANOS)));
				tipoProcedHospitalarInternoVO.setPhiDiag(phiDiag);
			}
		}
		return tipoProcedHospitalarInternoVO;	
	}
	
	private Integer idadePaciente (Integer codigoPaciente, Date dataFim) {
		AipPacientes paciente = aipPacientesDAO.pesquisarPacientePorCodigo(codigoPaciente);
		if (paciente != null) {
			return DateUtil.obterQtdMesesEntreDuasDatas(paciente.getDtNascimento(),dataFim)/12;
		}
		return null;
	}
	/**
	 *@ORADB: PPROCEDURE VER_ACOMP_IMPLANTE
	 *@author marcos.silva
	 * @throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 */
	private void verificaAcompImplante(FatResumoApacs resumoApacs) throws NumberFormatException, ApplicationBusinessException {
		returnVerificaLaudoOtorrino = null;
		Integer phiAcomimplInf = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_INFANTIL)));
		Integer phiAcomimplAdu = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_ADULTO)));
		Date dtFimComp = (Date) parametrosProcedure.get(AghuParametrosEnum.P_DT_FIM_COMP_APT);
		if (resumoApacs.getProcedimentoHospitalarInterno().getSeq().equals(phiAcomimplInf) || resumoApacs.getProcedimentoHospitalarInterno().getSeq().equals(phiAcomimplAdu)) {
			Integer implanteCoclear = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_IMPLANTE_COCLEAR)));  
			List<MbcCirurgias> mbcCirurgias = mbcCirurgiasDAO.obterImplante(resumoApacs.getId().getPacCodigo(), implanteCoclear);
			setrBuscaImplante(mbcCirurgias);
			if (mbcCirurgias != null && !mbcCirurgias.isEmpty()) {
				returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.IC;
			} else if (buscaQuantidadeApac(resumoApacs.getId().getPacCodigo(), resumoApacs.getProcedimentoHospitalarInterno(), dtFimComp).equals(DominioSimNao.N)) {
				returnVerificaLaudoOtorrino = null;
			}
		}
	}
	private DominioSimNao buscaQuantidadeApac(Integer pacCodigo, FatProcedHospInternos procedimentoHospitalarInterno, Date v_dt_fim_comp) throws NumberFormatException, ApplicationBusinessException {
		CaracteristicaPhiVO caracteristicaPhiVO = faturamentoFacade.fatcVerCaractPhi(Short.valueOf("1"), Byte.valueOf("2"), procedimentoHospitalarInterno.getSeq(), "Quantidade Apacs Ano");
		DominioSimNao verExiste = null;
		int quantidadeApac = 0;
		Integer phiDiagAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_DIAG_DEF_AUDITIVA)));
		Integer phiAdaptadoAdu = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_ADULTO)));
		Integer phiAdpAduAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_ADU_ANT)));
		Integer phiAdaptadoInf = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_INFANTIL)));
		Integer phiAdpInfAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_INF_ANT)));
		Integer phiAcomimplAdu = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_ADULTO)));
		Integer phiAcomimplAduAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_ADU_ANT)));
		Integer phiAcomimplInf = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_INFANTIL)));
		Integer phiAcomimplInfAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_IMPL_INF_ANT)));
		List<FatResumoApacs> apacs = fatResumoApacsDAO.obterApacs(pacCodigo, procedimentoHospitalarInterno.getSeq(), v_dt_fim_comp);
		for (int i = 0; i < apacs.size(); i++) {
			FatResumoApacs apac = apacs.get(i);
			if (procedimentoHospitalarInterno.getSeq() == phiDiag) {
				if (apac.getProcedimentoHospitalarInterno().getSeq() == phiDiagAnt) {
					quantidadeApac++;
				}
			} else if (procedimentoHospitalarInterno.getSeq() == phiAdaptadoAdu) {
				if (apac.getProcedimentoHospitalarInterno().getSeq() == phiAdpAduAnt) {
					quantidadeApac++;
				}
			} else if (procedimentoHospitalarInterno.getSeq() == phiAdaptadoInf) {
				if (apac.getProcedimentoHospitalarInterno().getSeq() == phiAdpInfAnt) {
					quantidadeApac++;
				}
			} else if (procedimentoHospitalarInterno.getSeq() == phiAcomimplAdu) {
				if (apac.getProcedimentoHospitalarInterno().getSeq() == phiAcomimplAduAnt) {
					quantidadeApac++;
				}
			} else if (procedimentoHospitalarInterno.getSeq() == phiAcomimplInf) {
				if (apac.getProcedimentoHospitalarInterno().getSeq() == phiAcomimplInfAnt) {
					quantidadeApac++;
				}
			} else if (apac.getProcedimentoHospitalarInterno().getSeq() == procedimentoHospitalarInterno.getSeq()) {
					quantidadeApac++;
			}
		}
		if (verExiste == DominioSimNao.N) {
			 return DominioSimNao.S;
		} else if (verExiste == DominioSimNao.S && quantidadeApac < caracteristicaPhiVO.getValorNumerico()) {
			 return DominioSimNao.S;
		} else {
			return DominioSimNao.N;
		}
	}

	/**
	 *@ORADB: PROCEDURE VER_APAC
	 *@author marcos.silva
	 */
	private DominioSinalizacaoControleFrequencia verificaApac(FatResumoApacs resumoApacs) {
		Integer phiDiagAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_DIAG_DEF_AUDITIVA)));
		Integer phiImplante = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_IMPLANTE_COCLEAR)));
		Integer phiAdaptado = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_ADULTO)));
		Integer phiAdpAnt = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_ADP_ADU_ANT)));
		Integer phiNadaptInf = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_NADP_INFANTIL)));
		Integer phiNadaptAdu = Integer.valueOf(String.valueOf(parametrosProcedure.get(AghuParametrosEnum.P_PHI_ACO_NADP_ADULTO)));
		if (resumoApacs.getProcedimentoHospitalarInterno().getSeq().equals(phiDiag) ||
				resumoApacs.getProcedimentoHospitalarInterno().getSeq().equals(phiDiagAnt)) {
			if (DateUtil.validaDataMenor(resumoApacs.getDtFinal(), dtAnterior)) {
				returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.RE;
			}
		}
		if (resumoApacs.getProcedimentoHospitalarInterno().getSeq() == phiImplante) {
			returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.IC;
		}
		if (resumoApacs.getProcedimentoHospitalarInterno().getSeq() == phiAdaptado || 
				resumoApacs.getProcedimentoHospitalarInterno().getSeq() == phiAdpAnt) {
			returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.AD;
		}
		if (resumoApacs.getIndAparelho() == DominioSimNao.S) {
			returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.AD;
		} else {
			if (resumoApacs.getProcedimentoHospitalarInterno().getSeq().equals(phiNadaptInf) || 
					resumoApacs.getProcedimentoHospitalarInterno().getSeq().equals(phiNadaptAdu)) {
				FatProcedAmbRealizadosVO ambRealizadosVO = fatProcedAmbRealizadoDAO.buscaAparelho(rPmr.getPacCodigo());
				if (ambRealizadosVO != null && ambRealizadosVO.getQuantidade() != null) {
					returnVerificaLaudoOtorrino = DominioSinalizacaoControleFrequencia.AD;
					setrAparelhos(ambRealizadosVO);
				}
			}
		}
		return returnVerificaLaudoOtorrino;
	}
	/**
	 * @ORADB: FUNCTION FATC_VER_TRANS_LAUDO
	 * @author marcos.silva
	 * #42011
	 */
	private String verificaTransplanteLaudo(Integer pacienteCodigo, Integer numero) throws ApplicationBusinessException {
		String v_trans =  "N";
		
		List<FatPacienteTransplantes> r_transp = fatPacienteTransplantesDAO.consultaPacientes(pacienteCodigo);
		if (r_transp != null) {
			for (int i = 0; i < r_transp.size(); i++) {
				AacConsultas aacConsultas = aacConsultasDAO.consulta(numero, r_transp.get(i).getId().getTtrCodigo(), r_transp.get(i).getDtTransplante());
				if (aacConsultas != null) {
					v_trans = "L";
				}
				String v_apac = aacAtendimentoApacsDAO.buscarApac(pacienteCodigo, r_transp.get(i).getId().getTtrCodigo());

				if (v_trans.equalsIgnoreCase("L")) {
					if (v_apac.equalsIgnoreCase("N")) {
						if (r_transp.get(i).getId().getTtrCodigo().equalsIgnoreCase("CORNEA")) {
							if (verificarQuantidadeApac(pacienteCodigo, r_transp.get(i).getId().getTtrCodigo(), r_transp.get(i).getDtTransplante()).equalsIgnoreCase("N")) {
								return v_trans;
							} else {
								return v_apac;
							}
						} else {
							if (verificarQuantidadeApac(pacienteCodigo, r_transp.get(i).getId().getTtrCodigo(), aacConsultas.getDtConsulta()).equalsIgnoreCase("N")) {
								return v_trans;
							} else {
								return v_apac;
							}
						}
					} else {
						return v_apac;
					}
				}
			}
		} else {
			return v_trans;
		}
		return v_trans;
	}
	
	private void preencherParametros() throws ApplicationBusinessException {
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_IMPLANTE_COCLEAR,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_IMPLANTE_COCLEAR));
		parametrosProcedure.put(AghuParametrosEnum.P_DT_FIM_COMP_APT,  parametroFacade.buscarValorData(AghuParametrosEnum.P_DT_FIM_COMP_APT));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_DIAG_DEF_AUDITIVA,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_DIAG_DEF_AUDITIVA));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_SELECAO,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_SELECAO));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_ADP_INFANTIL,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_ADP_INFANTIL));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_ADP_ADULTO,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_ADP_ADULTO));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_NADP_ADULTO,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_NADP_ADULTO));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_NADP_INFANTIL,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_NADP_INFANTIL));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_IMPL_INFANTIL,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_IMPL_INFANTIL));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_IMPL_ADULTO,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_IMPL_ADULTO));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_IMPL_INF_ANT,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_IMPL_INF_ANT));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_IMPL_ADU_ANT,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_IMPL_ADU_ANT));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_TERAPIA_ADULTO,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_TERAPIA_ADULTO));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_DIAG_DEF_MENOR_3ANOS,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_DIAG_DEF_MENOR_3ANOS));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_DIAG_DEF_MAIOR_3ANOS,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_DIAG_DEF_MAIOR_3ANOS));
		parametrosProcedure.put(AghuParametrosEnum.P_TRATAMENTO_OTORRINO,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_TRATAMENTO_OTORRINO));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_ADP_ADU_ANT,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_ADP_ADU_ANT));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_REAV_DIAG_MAIOR_3ANOS,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_REAV_DIAG_MAIOR_3ANOS));
		parametrosProcedure.put(AghuParametrosEnum.P_PHI_ACO_ADP_INF_ANT,  parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_PHI_ACO_ADP_INF_ANT));
	}

	private String verificarQuantidadeApac (Integer pacienteCodigo, String ttrCodigo, Date dateTransplante) throws ApplicationBusinessException {
		if (ttrCodigo.equalsIgnoreCase("CORNEA")) {
			BigDecimal valorNumerico = aghParametrosDAO.buscarParametro();
			Short paramentroTabelaPadrao = parametroFacade.buscarValorShort(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			Integer v_tct_seq = fatTipoCaractItensDAO.buscarSeqPorCaracteristica("Quantidade Apacs Ano");
			Long vContador = null;
			Long vContador2 = null;
			FatCaractItemProcHosp caractItemProcHosp = new FatCaractItemProcHosp();
			if (fatCaractItemProcHospDAO.existeFatCaractItemProcHosp(paramentroTabelaPadrao, valorNumerico, v_tct_seq, caractItemProcHosp)) {
				if (caractItemProcHosp.getValorNumerico() != null && caractItemProcHosp.getValorNumerico() > 0) {
					vContador = aacAtendimentoApacsDAO.contaApacs(pacienteCodigo, ttrCodigo, dateTransplante);
					vContador2 = aacAtendimentoApacsDAO.contaApacsHist(pacienteCodigo, ttrCodigo, dateTransplante);
					vContador = vContador + vContador2;
					if (vContador >= caractItemProcHosp.getValorNumerico()) {
						return "S";
					}
				}
			}

		}
		return "N";
	}
	
	
	/**
	 * Melhoria 50616 - Estoria 42803
	 * Preenche os Campos da Justificativa Do Procedimento Solicitado
	 * @param numeroConsulta
	 * @return LaudoSolicitacaoAutorizacaoProcedAmbVO
	 * @throws ApplicationBusinessException
	 * @author micael.coutinho
	 */
	public LaudoSolicitacaoAutorizacaoProcedAmbVO preencheCamposdaJustificativaDoProcedSolicitado(Integer numeroConsulta) throws ApplicationBusinessException{
		LaudoSolicitacaoAutorizacaoProcedAmbVO vo = new LaudoSolicitacaoAutorizacaoProcedAmbVO();
		vo = ambulatorioFacade.obterCidOtorrinoNumeroConsulta(numeroConsulta); //C22
		//nomeProfissionalSolicitante, -- CAMPO 41
		vo = ambulatorioFacade.obterNomeCPFProfissResponsavel(vo,numeroConsulta); //C23
		//aipc_get_cns_resp(pes.cpf) --CAMPO 44
		vo.setNumeroCnsProfissionalSolicitante(faturamentoFacade.getCnsResp(vo.getCpfProfissional()));
		return vo;
	}

	
	
	public List<ConsultaAmbulatorioVO> consultaAbaPacientesAusentes(Date dtPesquisa, Short zonaUnfSeq, VAacSiglaUnfSala zonaSala, AghEspecialidades especialidade, AghEquipes equipe,
			RapServidores profissional) throws ApplicationBusinessException {		
		Object[] objs=null;		
		List<ConsultaAmbulatorioVO> resultAusentes = getAacConsultasDAO().consultaAbaPacientesAusentes(dtPesquisa, zonaUnfSeq, zonaSala, especialidade, equipe, profissional);
		//Limita as descrições de equipe e especialidade para 30 caracteres e inclui o formato do número de prontuário.
		//Responsável também por realizar a consulta do profissional para exibição no tooltip.
		if(resultAusentes != null){
			for(int i=0;i<resultAusentes.size();i++){
				if(resultAusentes.get(i).getNomeEspecialidade().length() > 30){
					String nomeEspecialidade = resultAusentes.get(i).getNomeEspecialidade().substring(0, 29);
					resultAusentes.get(i).setNomeEspecialidade(nomeEspecialidade);					
				}
				if(resultAusentes.get(i).getGradeEquipeNome().length() > 30){
					String equipeNome = resultAusentes.get(i).getGradeEquipeNome().substring(0, 29);
					resultAusentes.get(i).setGradeEquipeNome(equipeNome);					
				}
				if(resultAusentes.get(i).getProntuario() != null){
					if(resultAusentes.get(i).getProntuario().toString().length() > 1){
						String palavra = (resultAusentes.get(i).getProntuario().toString());				
						char letra = palavra.charAt(palavra.length() - 1);
						resultAusentes.get(i).setMascaraProntuario(palavra.substring(0, palavra.length() - 1) + "/" + letra);						
					}else{
						resultAusentes.get(i).setMascaraProntuario(resultAusentes.get(i).getProntuario().toString());						
					}
				}									
				if(resultAusentes.get(i).getControleSerMatricula() != null && resultAusentes.get(i).getControleServinCodigo() != null){
				objs = prescricaoMedicaFacade.buscaConsProf(resultAusentes.get(i).getControleSerMatricula(),resultAusentes.get(i).getControleServinCodigo());			
				resultAusentes.get(i).setNomeProfissional(String.valueOf(objs[1]));
				}
			}
		}							
		return resultAusentes;
	}
	
	/**
	 * Function
	 * ORADB: AACP_BUSCA_CONJ
	 * @throws ApplicationBusinessException 
	 */
	public String obterNomeResponsavelMarcacaoConsulta(Integer numero)  {
		AacConsultas consulta = obterPorChavePrimaria(numero);
		if(consulta.getSituacaoConsulta().getSituacao().equals("M")) {
			if(consulta != null && consulta.getServidorMarcacao() != null) {
				return consulta.getServidorMarcacao().getPessoaFisica().getNome();
			}
			else {
				List<AacConsultasJn> consultaJn = ambulatorioFacade.listaConsultasJn(numero, "L");
				if(consultaJn !=null && !consultaJn.isEmpty()) {
					RapServidores servidor;
					try {
						servidor = registroColaboradorFacade.obterServidorPorUsuario(consultaJn.get(0).getNomeUsuario());
						if(servidor != null) {
							return servidor.getPessoaFisica().getNome();
						}
					} catch (ApplicationBusinessException e) {
						logError("Exceção capturada: ", e);
					}
				}	
				else {
					return consulta.getServidor().getPessoaFisica().getNome();
				}
			}
		}
		return "";
	}
	
	private List<ConsultaAmbulatorioVO> verificarDocumentoPendenteCertificacaoDigital(List<ConsultaAmbulatorioVO> list) {
		for (ConsultaAmbulatorioVO a : list) {
			AipPacientes p = pacienteFacade.obterAipPacientesPorChavePrimaria(a.getPacienteCodigo());
			if (this.getCertificacaoDigitalFacade().verificarExisteDocumentosPaciente(p)) {
				a.setStatusCertificacaoDigital(br.gov.mec.aghu.model.AacConsultas.StatusCertificaoDigital.PENDENTE);
			}
		}
		return list;
	}
	public void persistirControleImpressaoLaudo(ConsultaAmbulatorioVO consulta) throws ApplicationBusinessException {
		Integer parametroOftalmo = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_TRAT_OFTALMO);
		List<AacConsultas> consultaSinalizacaoControleFrequenciaFOTO = aacConsultasDAO.verificarNecessidadeSinalizacaoControleFrequenciaFoto(consulta.getNumero(), parametroOftalmo);
		
		if (consulta.getIndicaImagem().equalsIgnoreCase("C") && consultaSinalizacaoControleFrequenciaFOTO != null) {
			fatLaudosImpressaoDAO.persistir(getFatLaudosImpressaoConsulta(consulta));
		}
	}
	private FatLaudosImpressao getFatLaudosImpressaoConsulta(ConsultaAmbulatorioVO consultaAmbulatorioVO) {
		FatLaudosImpressao laudosImpressao = new FatLaudosImpressao();
		
		laudosImpressao.setPacCodigo(consultaAmbulatorioVO.getPacienteCodigo());
		laudosImpressao.setConsulta(aacConsultasDAO.listarConsulta(consultaAmbulatorioVO.getNumero()));
		laudosImpressao.setDataImpressao(new Date());
		laudosImpressao.setIndSituacao(DominioSituacao.A);
		laudosImpressao.setTipoSinalizacao(consultaAmbulatorioVO.getTipoSinalizacao());
		return laudosImpressao;
	}
	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return this.certificacaoDigitalFacade;
	}
	protected IParametroFacade getParametrosFacade() {
		return this.parametroFacade;
	}
	public AghEspecialidadesDAO getAghEspecialidadesDAO() {
		return aghEspecialidadesDAO;
	}
	public void setAghEspecialidadesDAO(AghEspecialidadesDAO aghEspecialidadesDAO) {
		this.aghEspecialidadesDAO = aghEspecialidadesDAO;
	}
	public FatContaApacDAO getFatContaApacDAO() {
		return fatContaApacDAO;
	}
	public void setFatContaApacDAO(FatContaApacDAO fatContaApacDAO) {
		this.fatContaApacDAO = fatContaApacDAO;
	}
	public FatPacienteTransplantesDAO getFatPacienteTransplantesDAO() {
		return fatPacienteTransplantesDAO;
	}
	public void setFatPacienteTransplantesDAO(FatPacienteTransplantesDAO fatPacienteTransplantesDAO) {
		this.fatPacienteTransplantesDAO = fatPacienteTransplantesDAO;
	}
	public AacAtendimentoApacsDAO getAacAtendimentoApacsDAO() {
		return aacAtendimentoApacsDAO;
	}
	public void setAacAtendimentoApacsDAO(AacAtendimentoApacsDAO aacAtendimentoApacsDAO) {
		this.aacAtendimentoApacsDAO = aacAtendimentoApacsDAO;
	}
	public AghParametrosDAO getAghParametrosDAO() {
		return aghParametrosDAO;
	}
	public void setAghParametrosDAO(AghParametrosDAO aghParametrosDAO) {
		this.aghParametrosDAO = aghParametrosDAO;
	}
	public FatTipoCaractItensDAO getFatTipoCaractItensDAO() {
		return fatTipoCaractItensDAO;
	}
	public void setFatTipoCaractItensDAO(FatTipoCaractItensDAO fatTipoCaractItensDAO) {
		this.fatTipoCaractItensDAO = fatTipoCaractItensDAO;
	}
	public FatCaractItemProcHospDAO getFatCaractItemProcHospDAO() {
		return fatCaractItemProcHospDAO;
	}
	public void setFatCaractItemProcHospDAO(FatCaractItemProcHospDAO fatCaractItemProcHospDAO) {
		this.fatCaractItemProcHospDAO = fatCaractItemProcHospDAO;
	}
	public IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	public MarcacaoConsultaON getMarcacaoConsultaON() {
		return marcacaoConsultaON;
	}
	public FatProcedAmbRealizadosVO getrPmr() {
		return rPmr;
	}
	public void setrPmr(FatProcedAmbRealizadosVO rPmr) {
		this.rPmr = rPmr;
	}
	public List<MbcCirurgias> getrBuscaImplante() {
		return rBuscaImplante;
	}
	public void setrBuscaImplante(List<MbcCirurgias> rBuscaImplante) {
		this.rBuscaImplante = rBuscaImplante;
	}
	public FatProcedAmbRealizadosVO getrAparelhos() {
		return rAparelhos;
	}
	public void setrAparelhos(FatProcedAmbRealizadosVO rAparelhos) {
		this.rAparelhos = rAparelhos;
	}
	
	
	public FatLaudosPacApacsDAO getFatLaudosPacApacsDAO() {
		return fatLaudosPacApacsDAO;
	}

	public void setFatLaudosPacApacsDAO(FatLaudosPacApacsDAO fatLaudosPacApacsDAO) {
		this.fatLaudosPacApacsDAO = fatLaudosPacApacsDAO;
	}
}