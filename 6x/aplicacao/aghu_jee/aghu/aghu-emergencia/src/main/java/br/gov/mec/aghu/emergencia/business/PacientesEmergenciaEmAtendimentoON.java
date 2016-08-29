package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.blococirurgico.service.IBlocoCirurgicoService;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.vo.PacAtendimentoVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.emergencia.vo.PacienteEmAtendimentoVO;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.vo.DadosPacientesEmAtendimentoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;
/**
 * Regras de negócio relacionadas ao Paciente em atendimento.
 * 
 * @author ihaas
 * 
 */
@Stateless
public class PacientesEmergenciaEmAtendimentoON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3423984755101821178L;
	
	@Inject
	private RapPessoasFisicasDAO rapPessoasFisicasDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@EJB
	private IExamesService examesService;
	
	@EJB
	private IBlocoCirurgicoService blocoCirurgicoService;
	
	@EJB 
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private PacientesEmergenciaON pacientesEmergenciaON;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private static final String INCLUIR_GESTACAO = "silk-icon silk-incluir-gestacao";
	private static final String IMPRIMIR_AF = "silk-icon silk-imprimir-af";
	private static final String MARCADOR_VERMELHO = "silk-icon silk-marcador-vermelho";
	private static final String PACIENTE_PROJETO_PESQUISA = "silk-icon silk-paciente-projeto-pesquisa";
	private static final String FICHA_ANESTESICA_CONCLUIDA = "silk-icon silk-ficha-anestesia-concluida";
	private static final String FICHA_ANESTESIA_RASCUNHO = "silk-icon silk-ficha-anestesia-rascunho";
	private static final String FICHA_ANESTESIA_PENDENTE = "silk-icon silk-ficha-anestesia-pendente";
	private static final String ASSINATURA_DIGITAL_PENDENTE = "silk-icon silk-assinatura-digital-pendente"; 
	
	private static final String TITLE_INCLUIR_GESTACAO = "Inclua gestação";
	private static final String TITLE_IMPRIMIR_AF = "Imprimir admissão";
	private static final String TITLE_MARCADOR_VERMELHO = "Solicite VDRL";
	private static final String TITLE_PACIENTE_PROJETO_PESQUISA = "Projeto de pesquisa";
	

	public List<PacienteEmAtendimentoVO> listarPacientesEmAtendimento(Short unfSeq) throws ApplicationBusinessException {
		
		List<PacienteEmAtendimentoVO> listVo = this.mamTrgEncInternoDAO.listarPacientesEmAtendimento(unfSeq);
		
		if (listVo != null && !listVo.isEmpty()) {
			for (PacienteEmAtendimentoVO vo : listVo) {
				PacAtendimentoVO dadosPac = this.obterDadosPacientesEmAtendimento(vo.getConNumero());
				if (dadosPac != null) {
					vo.setDtConsulta(dadosPac.getDtConsulta());
					vo.setAtdSeq(dadosPac.getAtdSeq());
					vo.setAtdSerMatricula(dadosPac.getAtdSerMatricula());
					vo.setAtdSerVinCodigo(dadosPac.getAtdSerVinCodigo());
					vo.setAtdUnfSeq(dadosPac.getAtdUnfSeq());
					vo.setEspSeq(dadosPac.getEspSeq());
					vo.setEspSigla(dadosPac.getEspSigla());
				}
				Paciente pac = this.pacientesEmergenciaON.obterPacientePorCodigo(vo.getPacCodigo());
				vo.setPacNome(pac.getNome());
				vo.setDtNascimentoPac(pac.getDtNascimento());
				vo.setProntuarioPac(pac.getProntuario());
				vo.setIdade(CoreUtil.calculaIdade(pac.getDtNascimento()));
				
				vo.setNomeResponsavel(this.buscarNomeResponsavelPorMatricula(vo.getAtdSerVinCodigo(), vo.getAtdSerMatricula()));
			}
			CoreUtil.ordenarLista(listVo, PacienteEmAtendimentoVO.Fields.PAC_NOME.toString(), true);
			CoreUtil.ordenarLista(listVo, PacienteEmAtendimentoVO.Fields.TEI_SEQP.toString(), false);
		}
		return listVo;
	}
	
	public PacAtendimentoVO obterDadosPacientesEmAtendimento(Integer conNumero)
			throws ApplicationBusinessException {

		PacAtendimentoVO result = new PacAtendimentoVO();
		DadosPacientesEmAtendimentoVO dadosVO = this.aghuFacade
				.obterDadosPacientesEmAtendimento(conNumero);

		if (dadosVO != null) {
			result.setDtConsulta(dadosVO.getDtConsulta());
			result.setAtdSeq(dadosVO.getAtdSeq());
			result.setAtdSerMatricula(dadosVO.getAtdSerMatricula());
			result.setAtdSerVinCodigo(dadosVO.getAtdSerVinCodigo());
			result.setAtdUnfSeq(dadosVO.getAtdUnfSeq());
			result.setEspSeq(dadosVO.getEspSeq());
			result.setEspSigla(dadosVO.getEspSigla());
		}
		return result;
	}
	
	private String buscarNomeResponsavelPorMatricula(Short codigo,
			Integer matricula) throws ApplicationBusinessException {
		String result = null;

		result = this.rapPessoasFisicasDAO.buscarNomeResponsavelPorMatricula(
				codigo, matricula);

		return result;
	}
	
	// RN06 - #864
	public void verificarNotificacaoGmr(PacienteEmAtendimentoVO pacienteSelecionado) throws ApplicationBusinessException{
		Boolean result = null;
	
			 result = this.mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(pacienteSelecionado.getPacCodigo());
	
			if(result){
				pacienteSelecionado.setDevePintar(Boolean.TRUE);
			}
	}
	
	
	// C3 - #864
	public Short obterUltimaGestacaoRegistrada(Integer pacCodigo){
		List<McoGestacoes> gestacoes = this.mcoGestacoesDAO.obterUltimaGestacaoRegistrada(pacCodigo);
		
		if(gestacoes != null && !gestacoes.isEmpty()){
			
			return gestacoes.get(0).getId().getSeqp();
		} else {
			return null;
		}
	}
	
	// RN07 - #864
	public String verificaImpressaoAdmissaoObstetrica(PacienteEmAtendimentoVO pacienteSelecionado, Short seqP){
		
		// C5 - #864
		McoLogImpressoes admissaoObstetrica = this.emergenciaFacade.verificaImpressaoAdmissaoObstetrica(pacienteSelecionado.getPacCodigo(), seqP);
		
		if(admissaoObstetrica != null){
			return DateUtil.obterDataFormatada(admissaoObstetrica.getCriadoEm(), "dd/MM/yyyy");
		} else {
			return DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy");
		}
	}
	
	public Integer obterMatricula(){
		return this.usuario.getMatricula();
	}
	
	public Short obterVinculo(){
		return this.usuario.getVinculo();
	}

	public void verificarUnfPmeInformatizada(Short unfSeq, PacienteEmAtendimentoVO pacienteSelecionado) throws ApplicationBusinessException {
		emergenciaFacade.elaborarPrescricaMedica(pacienteSelecionado.getConNumero(), unfSeq);
	}
	
	private Boolean verificarCaracteristicaUnidade(Short unfSeq, ConstanteAghCaractUnidFuncionais caracUnidade) throws ServiceException{
		return this.aghuFacade.existeCaractUnidFuncionaisPorSeqCaracteristica(unfSeq, caracUnidade);
	}
	
	public Boolean verificarUnfEmergObstetrica(Short unfSeq) throws ServiceException{
		return this.verificarCaracteristicaUnidade(unfSeq, ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA);
	}

	public void verificarSituacaoPacientes(List<PacienteEmAtendimentoVO> listaPacientes, Short unfSeq) throws ApplicationBusinessException, ServiceException {
		
		for (PacienteEmAtendimentoVO paciente : listaPacientes) {
			
			this.verificarSilkColuna10(paciente);
			this.verificarSilkColuna11(paciente);
			this.verificarSilkColuna12(paciente);
			this.verificarSilkColuna13(paciente);
			this.verificarSilkColuna14(paciente, unfSeq);
			this.verificarNotificacaoGmr(paciente);
		}
	}
	
	private void verificarSilkColuna10(PacienteEmAtendimentoVO paciente) throws ApplicationBusinessException{
		
		// RN01 - #864
		if(this.obterUltimaGestacaoRegistrada(paciente.getPacCodigo()) == null){
			paciente.setSilkColuna10(INCLUIR_GESTACAO);
			paciente.setLabelColuna10(TITLE_INCLUIR_GESTACAO);
		} else if(this.emergenciaFacade.verificaImpressaoAdmissaoObstetrica(paciente.getPacCodigo(), this.obterUltimaGestacaoRegistrada(paciente.getPacCodigo())) == null){
			paciente.setSilkColuna10(IMPRIMIR_AF);
			paciente.setLabelColuna10(TITLE_IMPRIMIR_AF);
		} else {
			paciente.setSilkColuna10(null);
		}
		
	}
	
	private void verificarSilkColuna11(PacienteEmAtendimentoVO paciente) throws ApplicationBusinessException{
		
		// RN02 - #864
		if(this.obterUltimaGestacaoRegistrada(paciente.getPacCodigo()) != null && !examesService.verificarExameVDRLnaoSolicitado(paciente.getAtdSeq())){
			paciente.setSilkColuna11(MARCADOR_VERMELHO);
			paciente.setLabelColuna11(TITLE_MARCADOR_VERMELHO);
		} else {
			paciente.setSilkColuna11(null);
		}
		
	}
	
	private void verificarSilkColuna12(PacienteEmAtendimentoVO paciente) throws ApplicationBusinessException{
		
		// RN03 - #864
		if(this.examesService.verificaPacienteEmProjetoPesquisa(paciente.getPacCodigo())){
			paciente.setSilkColuna12(PACIENTE_PROJETO_PESQUISA);
			paciente.setLabelColuna12(TITLE_PACIENTE_PROJETO_PESQUISA);
		}
		
	}
	
	private void verificarSilkColuna13(PacienteEmAtendimentoVO paciente) throws ApplicationBusinessException{
		
		// C7 - #864
		String pendente = this.blocoCirurgicoService.obterPendenciaFichaAnestesia(paciente.getAtdSeq());
		
		// RN04 - #864
		if(DominioIndPendenteAmbulatorio.R.toString().equalsIgnoreCase(pendente)){
			paciente.setSilkColuna13(FICHA_ANESTESIA_RASCUNHO);
		} else if(DominioIndPendenteAmbulatorio.P.toString().equalsIgnoreCase(pendente)){
			paciente.setSilkColuna13(FICHA_ANESTESIA_PENDENTE);
		} else if (DominioIndPendenteAmbulatorio.V.toString().equalsIgnoreCase(pendente)){
			paciente.setSilkColuna13(FICHA_ANESTESICA_CONCLUIDA);
		} else {
			paciente.setSilkColuna13(null);
		}
		
	}
	
	private void verificarSilkColuna14(PacienteEmAtendimentoVO paciente, Short unfSeq) throws ApplicationBusinessException, ServiceException{
		
		// RN05 - #864
		if(this.verificarUnfEmergObstetrica(unfSeq)){
			paciente.setSilkColuna14(ASSINATURA_DIGITAL_PENDENTE);
		}
		
	}
	
	/**
	 * Obtem a lista de receitas e atestados do paciente
	 * @param conNumero
	 *  
	*/
	public List<DocumentosPacienteVO> obterListaReceitasAtestadosPaciente(Integer conNumero) throws ApplicationBusinessException {
		List<DocumentosPacienteVO> listaDocumentos = new ArrayList<DocumentosPacienteVO>();
		
		AacConsultas consulta = ambulatorioFacade.obterConsulta(conNumero);
		Short espSeq = consulta.getGradeAgendamenConsulta().getEspecialidade().getSeq();
		
		// Receitas 
		Integer qtdReceituarios = 1;
		List<MamReceituarios> receituarios = mamReceituariosDAO.obterReceituariosPorNumeroConsultaEIndPendente(conNumero);
		for(MamReceituarios receituario : receituarios) {
			if(receituario.getPendente()==DominioIndPendenteAmbulatorio.V || receituario.getPendente()==DominioIndPendenteAmbulatorio.P) {
				DocumentosPacienteVO documentoReceituario = setarDadosDocumentoPaciente("Receita "+qtdReceituarios,receituario.getIndImpresso().isSim());
				documentoReceituario.setReceituarios(receituario);
				listaDocumentos.add(documentoReceituario);
				qtdReceituarios++;
			}
		}
		
		// Atestados
		ambulatorioFacade.obterListaDocumentosPacienteAtestados(conNumero, listaDocumentos);
		
		ambulatorioFacade.obterListaReceituarioCuidado(conNumero, listaDocumentos, espSeq, false);

		return listaDocumentos;
	}
	
	public DocumentosPacienteVO setarDadosDocumentoPaciente(String descricao, Boolean imprimiu) {
		DocumentosPacienteVO documento = new DocumentosPacienteVO();

		documento.setDescricao(descricao);
		if (imprimiu) {
			documento.setImprimiu(Boolean.TRUE);
		} else {
			documento.setImprimiu(Boolean.FALSE);
		}

		return documento;
	}
}