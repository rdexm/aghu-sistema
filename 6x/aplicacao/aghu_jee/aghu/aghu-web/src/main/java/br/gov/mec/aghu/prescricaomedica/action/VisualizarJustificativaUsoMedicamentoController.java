package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AvaliacaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VMpmServConselhoGeralVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


/**
 * #1292 Prescrição: Visualizar Justificativa do Uso de Medicamento.
 * 
 */
public class VisualizarJustificativaUsoMedicamentoController extends ActionReport {

	private static final long serialVersionUID = 8572512766546035529L;
	
	private static final Log LOG = LogFactory.getLog(VisualizarJustificativaUsoMedicamentoController.class);

	private static final String NAVIGATION_PAGE_VISUALIZAR_JUSTIFICATIVA = "prescricaomedica-visualizarJustificativaUsoMedicamento";
	
	private static final String NAVIGATION_PAGE_PESQUISAR_SOLICITACOES = "prescricaomedica-pesquisarSolicitacoesUsoMedicamentoList";
	
	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/prescricaomedica/report/relatorioSolicitacaoMedicamentosAvaliar.jasper";

	@EJB
	private IPermissionService permissionService;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * Parâmetros de conversação
	 */
	private Integer jumSeq;
	private Integer matCodigo;
	
	private MpmJustificativaUsoMdto justificativa;
	private VMpmServConselhoGeralVO dadosMedico;
	private List<AvaliacaoMedicamentoVO> listaAvaliacaoMedicamentoVO;
	private AvaliacaoMedicamentoVO avaliacaoMedicamentoVO;

	private boolean permissaoCandAprovLote;
	/**
	 * Inicializa a conversação.
	 */
	@PostConstruct
	public void init() {

		begin(conversation);
	}

	/**
	 * Método responsável por inicializar a tela.
	 * 
	 * @return Navegação para a tela de Justificativa
	 */
	public String iniciar() {
		
		justificativa = this.prescricaoMedicaFacade.obterDadosJustificativaUsoMedicamento(jumSeq);

		if (justificativa != null && justificativa.getServidorValida() != null) {
			dadosMedico = this.registroColaboradorFacade.obterServidorConselhoGeralPorIdServidor(justificativa.getServidorValida().getId().getMatricula(),
					justificativa.getServidorValida().getId().getVinCodigo());
		}
		
		listaAvaliacaoMedicamentoVO = new ArrayList<AvaliacaoMedicamentoVO>();

		permissaoCandAprovLote = this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "alterarSoliciacoesUsoMdtos", "executar");
		if (DominioSituacaoSolicitacaoMedicamento.P.equals(justificativa.getSituacao()) && permissaoCandAprovLote) {
			justificativa.setCandAprovLote(Boolean.TRUE);
			justificativa.setSituacao(DominioSituacaoSolicitacaoMedicamento.T);
			justificativa.setServidorConhecimento(servidorLogadoFacade.obterServidorLogado());

			prescricaoMedicaFacade.atualizarMpmJustificativaUsoMdto(justificativa);
		}

		return NAVIGATION_PAGE_VISUALIZAR_JUSTIFICATIVA;
	}
	
	public boolean verificarPermissao(){
		try {
			if(justificativa != null && justificativa.getGupSeq() == this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GRPO_USO_MDTO_TB).getVlrNumerico().shortValue()){			
				return true;
			}
			return false;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return false;
	}

	/**
	 * Calcula a idade do paciente.
	 * 
	 * @return Idade do paciente
	 */
	public String obterIdadePaciente() {
		return prescricaoMedicaFacade.obterIdadeFormatada(justificativa.getAtendimento().getPaciente().getDtNascimento());
	}

	/**
	 * Obtém a Unidade do Atendimento.
	 * 
	 * @return Unidade do Atendimento
	 */
	public String obterUnidadeAtendimento() {

		if (justificativa == null || justificativa.getAtendimento() == null || justificativa.getAtendimento().getUnidadeFuncional() == null) {
			return null;
		}

		AghUnidadesFuncionais unidade = justificativa.getAtendimento().getUnidadeFuncional();

		return unidade.getAndarAlaDescricao();
	}

	/**
	 * Atualiza a flag Candidata para Aprovação em Lote quando alterada.
	 */
	public void atualizarCheckAprovLote() {

		justificativa = this.prescricaoMedicaFacade.obterDadosJustificativaUsoMedicamento(jumSeq);

		if(!justificativa.getSituacao().equals(DominioSituacaoSolicitacaoMedicamento.T)){
			apresentarMsgNegocio(Severity.ERROR, "MPM-00862");
			justificativa.setCandAprovLote(null);
		}else{
			if (Boolean.TRUE.equals(justificativa.getCandAprovLote())) {
				justificativa.setCandAprovLote(Boolean.FALSE);
			} else {
				justificativa.setCandAprovLote(Boolean.TRUE);
			}

			prescricaoMedicaFacade.atualizarMpmJustificativaUsoMdto(justificativa);
		}
		
	}

//	private int calcularQuantidadeDiasIdade(Calendar hoje, Calendar nascimento) {
//
//		int dias = hoje.get(Calendar.DAY_OF_MONTH) - nascimento.get(Calendar.DAY_OF_MONTH);
//
//		if (dias < 0) {
//			switch (hoje.get(Calendar.MONTH)) {
//				case Calendar.FEBRUARY:
//				case Calendar.APRIL:
//				case Calendar.JUNE:
//				case Calendar.AUGUST:
//				case Calendar.SEPTEMBER:
//				case Calendar.NOVEMBER:
//				case Calendar.JANUARY:
//					dias += 31;
//					break;
//				case Calendar.MAY:
//				case Calendar.JULY:
//				case Calendar.OCTOBER:
//				case Calendar.DECEMBER:
//					dias += 30;
//					break;
//				case Calendar.MARCH:
//					if (hoje.get(Calendar.YEAR) % 400 == 0 || (hoje.get(Calendar.YEAR) % 4 == 0 && hoje.get(Calendar.YEAR) % 100 != 0)) {
//						dias += 29;
//					} else {
//						dias += 28;
//					}
//					break;
//			}
//		}
//
//		return dias;
//	}

	/**
	 * Obtém informação do Convênio.
	 * 
	 * @return String contendo informação do Convênio
	 */
	public String obterConvenio() {

		if (justificativa != null && justificativa.getAtendimento() != null) {
			return examesFacade.obterConvenioAtendimento(justificativa.getAtendimento().getSeq());
		}
		
		return null;
	}

	/**
	 * Obtém detalhes da Justificativa.
	 * 
	 * @return String contendo detalhes da Justificativa
	 */
	public String obterJustificativa() {

		try {
			return prescricaoMedicaFacade.obterDetalhesJustificativaUsoMedicamento(jumSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	/**
	 * Retorna para a tela de pesquisa.
	 * 
	 * @return Navegação para a tela de Pesquisa
	 */
	public String voltar() {

		return NAVIGATION_PAGE_PESQUISAR_SOLICITACOES;
	}
	
	@Override
	protected Collection<AvaliacaoMedicamentoVO> recuperarColecao(){
		try {
			this.avaliacaoMedicamentoVO = prescricaoMedicaFacade.imprimirSolicitacaoMedicamentoAvaliar(justificativa);

			if(StringUtils.isEmpty(avaliacaoMedicamentoVO.getLocalizacao())){				
				avaliacaoMedicamentoVO.setLocalizacao(obterUnidadeAtendimento());
			}
			carregarCamposFuncoes(avaliacaoMedicamentoVO);
			listaAvaliacaoMedicamentoVO.add(avaliacaoMedicamentoVO);
		} catch (ApplicationBusinessException e) {
			if(e.getMessage().contains("LOCAL_ATENDIMENTO_NAO_ENCONTRADO")){
				apresentarMsgNegocio(Severity.ERROR, "LOCAL_ATENDIMENTO_NAO_ENCONTRADO");
			}else{
				apresentarExcecaoNegocio(e);
			}
		}
		return this.listaAvaliacaoMedicamentoVO;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return URL_DOCUMENTO_JASPER;
	}
	
	private void carregarCamposFuncoes(
			AvaliacaoMedicamentoVO avaliacaoMedicamentoVO)
			throws ApplicationBusinessException {
		avaliacaoMedicamentoVO.setExibeJustificativa(this.prescricaoMedicaFacade.justificativaAntiga
				(avaliacaoMedicamentoVO.getIndicacao(), avaliacaoMedicamentoVO.getInfeccaoTratar(), 
						avaliacaoMedicamentoVO.getDiagnostico()));
		avaliacaoMedicamentoVO.setExibeUsoRestrAntimicrobianoIgualN(this.prescricaoMedicaFacade.
				usoRestriAntimicrobianoIgualNao(avaliacaoMedicamentoVO.getGupSeq(), avaliacaoMedicamentoVO.getIndicacao()));
		avaliacaoMedicamentoVO.setExibeUsoRestrAntimicrobianoIgualS(this.prescricaoMedicaFacade.
				usoRestriAntimicrobianoIgualSim(avaliacaoMedicamentoVO.getGupSeq(), avaliacaoMedicamentoVO.getInfeccaoTratar()));
		avaliacaoMedicamentoVO.setExibeNaoPadronAntimicrobianoIgualN(this.prescricaoMedicaFacade.
				naoPadronAntimicrobianoIgualNao(avaliacaoMedicamentoVO.getGupSeq(), avaliacaoMedicamentoVO.getIndicacao(), 
						avaliacaoMedicamentoVO.getInfeccaoTratar()));
		avaliacaoMedicamentoVO.setExibeNaoPadronAntimicrobianoIgualS(this.prescricaoMedicaFacade.
				naoPadronAntimicrobianoIgualSim(avaliacaoMedicamentoVO.getGupSeq(), avaliacaoMedicamentoVO.getIndicacao(), 
						avaliacaoMedicamentoVO.getInfeccaoTratar()));
		avaliacaoMedicamentoVO.setExibeQuimioterapico(this.prescricaoMedicaFacade.
				indQuimioterapicoIgualSim(avaliacaoMedicamentoVO.getDiagnostico()));
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		
		Map<String, Object> parametros = new HashMap<String, Object>();
		
		try {				
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);

			parametros.put("NOME_HOSPITAL", parametroRazaoSocial.getVlrTexto());
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return parametros;
	}
	
	public void directPrint() throws ApplicationBusinessException {
		try {
			listaAvaliacaoMedicamentoVO = new ArrayList<AvaliacaoMedicamentoVO>();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/*
	 * Getters and Setters
	 */

	public MpmJustificativaUsoMdto getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(MpmJustificativaUsoMdto justificativa) {
		this.justificativa = justificativa;
	}

	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public VMpmServConselhoGeralVO getDadosMedico() {
		return dadosMedico;
	}

	public void setDadosMedico(VMpmServConselhoGeralVO dadosMedico) {
		this.dadosMedico = dadosMedico;
	}
	
	public List<AvaliacaoMedicamentoVO> getListaAvaliacaoMedicamentoVO() {
		return listaAvaliacaoMedicamentoVO;
	}

	public void setListaAvaliacaoMedicamentoVO(
			List<AvaliacaoMedicamentoVO> listaAvaliacaoMedicamentoVO) {
		this.listaAvaliacaoMedicamentoVO = listaAvaliacaoMedicamentoVO;
	}

	public AvaliacaoMedicamentoVO getAvaliacaoMedicamentoVO() {
		return avaliacaoMedicamentoVO;
	}

	public void setAvaliacaoMedicamentoVO(
			AvaliacaoMedicamentoVO avaliacaoMedicamentoVO) {
		this.avaliacaoMedicamentoVO = avaliacaoMedicamentoVO;
	}
	
}
