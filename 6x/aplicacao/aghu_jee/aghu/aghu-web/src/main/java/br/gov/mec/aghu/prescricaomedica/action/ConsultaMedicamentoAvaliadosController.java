package br.gov.mec.aghu.prescricaomedica.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;







import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AvaliacaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.PareceresMedicamentosVO;
import br.gov.mec.aghu.vo.AghAtendimentosVO;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;


public class ConsultaMedicamentoAvaliadosController extends ActionReport {


	private static final long serialVersionUID = -5227302505353350030L;
	
	private static final Log LOG = LogFactory.getLog(ConsultaMedicamentoAvaliadosController.class);
	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private AinLeitos ainLeitos;
	
	private AghAtendimentos aghAtendimentos;
	
	private List<MpmItemPrescParecerMdto> lista;
	
	private MpmItemPrescParecerMdto itemGridMedicamentos;
	
	private List<AghAtendimentosVO> aghAtendimentosVO;
	
	private AghAtendimentosVO aghAtendimentosVOSelecionado;
	
	private PareceresMedicamentosVO pareceresMedicamentosVO;


	private Integer codigoPaciente;
	
	private Date parametroDataFim;
	
	private List<AvaliacaoMedicamentoVO> listaAvaliacaoMedicamentoVO = new ArrayList<AvaliacaoMedicamentoVO>();
	
	private AvaliacaoMedicamentoVO avaliacaoMedicamentoVO;
	
	//componete pesquisa Fonetica
	private AipPacientes paciente;
	private Integer atdSeq; //#1307 estoria
	private Integer prontuario;
	
	private static final String BOTAO_VOLTAR = "prescricaomedica-consultaMedicamentosAvaliados";
	private static final String REDIRECIONA_VISUALIZAR_IMPRESSAO = "prescricaomedica-relatorioMedicamentosAvaliados"; 
	private static final String REDIRECIONA_DETALHAR = "prescricaomedica-detalhar";
	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/prescricaomedica/report/relatorioAvaliacaoMedicamentos.jasper";
	private static final String PERMISSAO_ATUALIZAR_RELATORIO_AVALIACAO_MEDICAMENTOS = "atualizarRelatorioAvaliacaoMedicamentos";
	private static final String ACAO_ATUALIZAR = "atualizar";
	

	private boolean permissaoAtualizarRelatorioAvaliacaoMedicamentos;
	private boolean pesquisaFonetica;
	private boolean gridTela;
	private boolean gridTelaAtendimentos;
	
	
	private StreamedContent media;


	private String part1;
	private String part2;
	private String part3;
	private String part4;
	private String part5;
	
	private final static String ESPACO = " ";
	private final static String VAZIO = "";
	
	
	public enum ConsultaMedicamentoAvaliadosControllerExceptionCode implements BusinessExceptionCode { 
		SELECIONAR_AO_MENOS_UM_FILTRO,
		ATENDIMENTO_NAO_ENCONTRADO,
		PACIENTE_NAO_ENCONTRADO_POR_LEITO
	}

	
	@PostConstruct
	public void iniciar() {
		this.begin(conversation);
	}
	
	public void inicio(){
		limparPartes();
		this.obterParametroDataFim();
		this.permissaoAtualizarRelatorioAvaliacaoMedicamentos = usuarioTemPermissao(PERMISSAO_ATUALIZAR_RELATORIO_AVALIACAO_MEDICAMENTOS);
		if(prontuario!=null){
//			aghAtendimentos = prescricaoMedicaFacade.obterAghAtendimentosPorProntuario(prontuario, parametroDataFim);
			paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codigoPaciente);
//			ainLeitos=null;
//			if(aghAtendimentos!=null){
//				//ainLeitos = aghAtendimentos.getLeito();
//			}
		}		
	}
	
	/**
	 * Invoca metodo da arquitetura para validar permissão do usuario.
	 */
	private boolean usuarioTemPermissao(String componente) {
		return this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), componente, ACAO_ATUALIZAR);
	}
	
	public void populaCabecalho(){
		
			pesquisaFonetica=false;		
	}
	
	public void obterParametroDataFim(){
		 AghParametros temp = new AghParametros();
		try {
			temp = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TEMPO_CONS_PCER_MDTO);
			Date hoje= new Date();
			parametroDataFim = DateUtils.addDays(hoje, -temp.getVlrNumerico().intValue());
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_IMPRESSAO", e.getMessage());
		}
		 
	}
	
	
	public void limpaCabecalho(){
		//paciente=null;
		codigoPaciente=null;
		//pesquisaFonetica=false;
		//ainLeitos=null;	
		
		aghAtendimentos=null;
		
	}
		
	public void pesquisarAtendimentos(){
		
	if(paciente !=null || ainLeitos != null){
		aghAtendimentosVO = prescricaoMedicaFacade.pesquisarAghAtendimentosPorProntuario(paciente != null?paciente.getProntuario():null,ainLeitos!=null?
				ainLeitos.getLeitoID():null,parametroDataFim);
		gridTelaAtendimentos=true;
		gridTela=false;
		aghAtendimentosVOSelecionado=null;
	}
		else {
			this.apresentarMsgNegocio(Severity.ERROR, 
					ConsultaMedicamentoAvaliadosControllerExceptionCode.SELECIONAR_AO_MENOS_UM_FILTRO.toString());
		}
	}
	
	public void pesquisarMedicamentosAvaliadosPaciente(){
		gridTela=true;
		setLista(prescricaoMedicaFacade.pesquisarMpmItemPrescParecerMdtoPorProtuarioLeito(aghAtendimentosVOSelecionado.getProntuario(),parametroDataFim,aghAtendimentosVOSelecionado.getSeq()));
	}
	
	public String concatenaValores(AghAtendimentosVO item){
		String parte1 = VAZIO;
		String parte2 = VAZIO;
		String parte3 = VAZIO;
		if(item != null ){
			parte1 = item.getAndar() !=null? item.getAndar().toString()+ESPACO:VAZIO;
			parte2 = item.getAghAla() !=null? item.getAghAla().getCodigo()+ESPACO+"-"+ESPACO:VAZIO;
			parte3  = item.getDescricao() !=null? item.getDescricao():VAZIO;
		}
		return parte1+parte2+parte3;
	}
	
	
	public String redirecionarPesquisaFonetica(){
		return "paciente-pesquisaPacienteComponente";
	}
		
	//SB01
	public List<AinLeitos> pesquisarLeitos(String param) {
		prontuario=null;
		return returnSGWithCount(prescricaoMedicaFacade.pesquisarLeitosPorUnidadeFuncionalSalaQuarto(param),prescricaoMedicaFacade.countPesquisarLeitosPorUnidadeFuncionalSalaQuarto(param));
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
//			if(paciente!=null){
//				pesquisaFonetica=true;
//				ainLeitos=null;
//				aghAtendimentos = prescricaoMedicaFacade.obterAghAtendimentosPorProntuario(paciente.getProntuario(), parametroDataFim);
//				if(aghAtendimentos != null && aghAtendimentos.getLeito().getLeitoID()!=null){
//					//ainLeitos = prescricaoMedicaFacade.obterLeitoQuartoUnidadeFuncionalPorId(aghAtendimentos.getLeito().getLeitoID());
//				}
//			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void limparCampos(){
		setLista(null);
		aghAtendimentosVO=null;
		aghAtendimentos=null;
		paciente=null;
		ainLeitos=null;
		prontuario=null;
		pesquisaFonetica=false;
		gridTela=false;
		gridTelaAtendimentos=false;
	}
	
	public String voltar(){
		return BOTAO_VOLTAR;
	}
	
	public String redirecionaVisualizar() throws JRException, IOException, DocumentException{
		listaAvaliacaoMedicamentoVO = new ArrayList<AvaliacaoMedicamentoVO>();
		DocumentoJasper documento;
		try {
			documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return REDIRECIONA_VISUALIZAR_IMPRESSAO;
	}
	
	@Override
	protected Collection<AvaliacaoMedicamentoVO> recuperarColecao()
			throws ApplicationBusinessException {
		listaAvaliacaoMedicamentoVO = new ArrayList<AvaliacaoMedicamentoVO>();
		avaliacaoMedicamentoVO = this.prescricaoMedicaFacade.imprimirAvaliacaoMedicamento(
				itemGridMedicamentos.getMpmParecerUsoMdtos().getJumSeq().intValue(), 
				itemGridMedicamentos.getMpmItemPrescricaoMdto().getMedicamento().getMatCodigo().intValue());
		carregarCamposFuncoes(avaliacaoMedicamentoVO);
		
		listaAvaliacaoMedicamentoVO.add(avaliacaoMedicamentoVO);
		return listaAvaliacaoMedicamentoVO;
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
	protected String recuperarArquivoRelatorio() {
		return URL_DOCUMENTO_JASPER;
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
	
	/**
	 * Método para imprimir Relatório de Avaliação de Medicamentos.
	 * Estória #45269.
	 */
	public void directPrint() throws ApplicationBusinessException {
		try {
			listaAvaliacaoMedicamentoVO = new ArrayList<AvaliacaoMedicamentoVO>();
			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			if(permissaoAtualizarRelatorioAvaliacaoMedicamentos){//itemGridMedicamentos.getMpmParecerUsoMdtos().getJumSeq()
				this.prescricaoMedicaFacade.atualizarSituacaoMedicamento(avaliacaoMedicamentoVO.getJumSeq(), avaliacaoMedicamentoVO.getCriadoEm(), 
						avaliacaoMedicamentoVO.getCriadoEm(), avaliacaoMedicamentoVO.getResponsavelAvaliacao());
			}
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);

		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (IOException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (DocumentException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} 
	}
	
	public String redirecionaDetalhar(){
		pareceresMedicamentosVO.getJumSeq().intValue();
		pareceresMedicamentosVO.getMedCodigo().intValue();
		return REDIRECIONA_DETALHAR;
	}
	
	public String obterStringReduziada(String nome, int tamanho){
		if(nome!=null && nome.length()>tamanho){
			return nome.substring(0,tamanho)+"...";
		}
		
		return nome;
	}
	

	public String formatarTooltip(String nome,int tamanho){
		limparPartes();
		
		if(nome.length()<=tamanho){
			part1=nome;
			return part1;
		}
		else{
			part1=nome.substring(0,tamanho*1);
		}
		if(nome.length()>=tamanho*2){
			part2=nome.substring((tamanho*1),tamanho*2);
		}
		else{
			part2=nome.substring((tamanho*1),nome.length());
			return part1;
		}
		if(nome.length()>=tamanho*3){
			part3=nome.substring((tamanho*2),tamanho*3);
		}
		else{
			part3=nome.substring((tamanho*2),nome.length());
			return part1;
		}
		if(nome.length()>=tamanho*4){
			part4=nome.substring((tamanho*3),tamanho*4);
		}
		else{
			part4=nome.substring((tamanho*3),nome.length());
			return part1;
		}
		if(nome.length()>=tamanho*5){
			part5=nome.substring((tamanho*4),tamanho*5);
		}
		else{
			part5=nome.substring((tamanho*4),nome.length());
			return part1;
		}
		
		
		return part1;
	}

	private void limparPartes() {
		part1="";
		part2="";
		part3="";
		part4="";
		part5="";
	}

	
	
	// GETS E SETS
	public AinLeitos getAinLeitos() {
		return ainLeitos;
	}
	
	public void setAinLeitos(AinLeitos ainLeitos) {
		this.ainLeitos = ainLeitos;
	}
	
	public AghAtendimentos getAghAtendimentos() {
		return aghAtendimentos;
	}
	
	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}
	
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	
	public AipPacientes getPaciente() {
		return paciente;
	}
	
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public Date getParametroDataFim() {
		return parametroDataFim;
	}
	
	
	public void setParametroDataFim(Date parametroDataFim) {
		this.parametroDataFim = parametroDataFim;
	}
	
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public boolean isPermissaoAtualizarRelatorioAvaliacaoMedicamentos() {
		return permissaoAtualizarRelatorioAvaliacaoMedicamentos;
	}

	public void setPermissaoAtualizarRelatorioAvaliacaoMedicamentos(
			boolean permissaoAtualizarRelatorioAvaliacaoMedicamentos) {
		this.permissaoAtualizarRelatorioAvaliacaoMedicamentos = permissaoAtualizarRelatorioAvaliacaoMedicamentos;
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
	
	public boolean isPesquisaFonetica() {
		return pesquisaFonetica;
	}

	public void setPesquisaFonetica(boolean pesquisaFonetica) {
		this.pesquisaFonetica = pesquisaFonetica;
	}
	

	public boolean isGridTela() {
		return gridTela;
	}

	public void setGridTela(boolean gridTela) {
		this.gridTela = gridTela;
	}
	public String getPart1() {
		return part1;
	}

	public void setPart1(String part1) {
		this.part1 = part1;
	}

	public String getPart2() {
		return part2;
	}

	public void setPart2(String part2) {
		this.part2 = part2;
	}

	public String getPart3() {
		return part3;
	}

	public void setPart3(String part3) {
		this.part3 = part3;
	}

	public String getPart4() {
		return part4;
	}

	public void setPart4(String part4) {
		this.part4 = part4;
	}

	public String getPart5() {
		return part5;
	}

	public void setPart5(String part5) {
		this.part5 = part5;
	}
	
	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	
	public PareceresMedicamentosVO getPareceresMedicamentosVO() {
		return pareceresMedicamentosVO;
	}

	public void setPareceresMedicamentosVO(
			PareceresMedicamentosVO pareceresMedicamentosVO) {
		this.pareceresMedicamentosVO = pareceresMedicamentosVO;
	}

	public List<AghAtendimentosVO> getAghAtendimentosVO() {
		return aghAtendimentosVO;
	}

	public void setAghAtendimentosVO(List<AghAtendimentosVO> aghAtendimentosVO) {
		this.aghAtendimentosVO = aghAtendimentosVO;
	}

	public boolean isGridTelaAtendimentos() {
		return gridTelaAtendimentos;
	}

	public void setGridTelaAtendimentos(boolean gridTelaAtendimentos) {
		this.gridTelaAtendimentos = gridTelaAtendimentos;
	}

	public AghAtendimentosVO getAghAtendimentosVOSelecionado() {
		return aghAtendimentosVOSelecionado;
	}

	public void setAghAtendimentosVOSelecionado(
			AghAtendimentosVO aghAtendimentosVOSelecionado) {
		this.aghAtendimentosVOSelecionado = aghAtendimentosVOSelecionado;
	}

	public List<MpmItemPrescParecerMdto> getLista() {
		return lista;
	}

	public void setLista(List<MpmItemPrescParecerMdto> lista) {
		this.lista = lista;
	}

	public MpmItemPrescParecerMdto getItemGridMedicamentos() {
		return itemGridMedicamentos;
	}

	public void setItemGridMedicamentos(MpmItemPrescParecerMdto itemGridMedicamentos) {
		this.itemGridMedicamentos = itemGridMedicamentos;
	}
}