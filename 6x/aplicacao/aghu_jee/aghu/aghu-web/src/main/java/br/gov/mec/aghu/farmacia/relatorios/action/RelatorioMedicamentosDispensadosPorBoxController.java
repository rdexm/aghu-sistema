package br.gov.mec.aghu.farmacia.relatorios.action;

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
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.MedicamentoDispensadoPorBoxVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AfaGrupoApresMdtos;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import net.sf.jasperreports.engine.JRException;

//Estória #5714
public class RelatorioMedicamentosDispensadosPorBoxController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 6672621898825811603L;

	private static final String PAGE_RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX= "relatorioMedicamentosDispensadosPorBox";
	private static final String PAGE_RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX_PDF= "relatorioMedicamentosDispensadosPorBoxPdf";
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;	
	
	private Date dataEmissaoInicio;	
	private Date dataEmissaoFim;
	private AfaMedicamento medicamento;
	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento;
	private AfaGrupoApresMdtos grupoApresMdtos;
	private AghMicrocomputador estacaoDispensadora;
	
	private Integer pacCodigo;
	private Integer prontuario;
	private AipPacientes paciente;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void iniciarPagina(){
	 
		
		processaBuscaPaciente();
	
	}
	
	public void processaBuscaPaciente(){
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
				if (paciente != null) {
					prontuario = paciente.getProntuario();
					pacCodigo = paciente.getCodigo();
				}
			}
		}else if(prontuario != null){
			try {
				paciente = farmaciaDispensacaoFacade.obterPacienteComAtendimentoPorProntuarioOUCodigo(prontuario,pacCodigo);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
			
	}
	public List<AghMicrocomputador> pesquisarEstacaoDispensadora(String estacao){
		return this.returnSGWithCount(this.administracaoFacade.pesquisarTodosMicroAtivos(estacao),pesquisarEstacaoDispensadoraCount(estacao));
	}
	
	public Long pesquisarEstacaoDispensadoraCount(String estacao){
		return this.administracaoFacade.pesquisarTodosMicroAtivosCount(estacao);
	}
		
	public List<AfaTipoApresentacaoMedicamento> pesquisarTipoApresentacaoMdtos(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.pesquisarTipoApresMdtos(strPesquisa),pesquisarTipoApresentacaoMdtosCount(strPesquisa));
	}
	
	public Long pesquisarTipoApresentacaoMdtosCount(String strPesquisa) {
		return this.farmaciaFacade.pesquisarTipoApresMdtosCount(strPesquisa);
	}
		
	public List<AfaGrupoApresMdtos> pesquisarGrupoApresentacaoMdtos(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.pesquisarGrupoApresMdtos(strPesquisa),pesquisarGrupoApresentacaoMdtosCount(strPesquisa));
	}
	
	public Long pesquisarGrupoApresentacaoMdtosCount(String strPesquisa) {
		return this.farmaciaFacade.pesquisarGrupoApresMdtosCount(strPesquisa);
	}
	
	public List<AfaGrupoMedicamento> pesquisarGrupoMedicamento(Object strPesquisa) {
		return this.farmaciaFacade.pesquisaGruposMedicamentos(strPesquisa);
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<AfaMedicamento> pesquisarMedicamentos(String strPesquisa) {
		return this.returnSGWithCount(this.farmaciaFacade.pesquisarListaMedicamentos(strPesquisa),pesquisarMedicamentosCount(strPesquisa));
	}
	
	public Long pesquisarMedicamentosCount(String strPesquisa) {
		return this.farmaciaFacade.pesquisarListaMedicamentosCount(strPesquisa);
	}
	
	/**
	 * Dados que serão impressos 
	 */
	private List<MedicamentoDispensadoPorBoxVO> colecao = new ArrayList<MedicamentoDispensadoPorBoxVO>();
	
	public void limparPesquisa() {
		this.dataEmissaoInicio = null;
		this.dataEmissaoFim = null;
		this.estacaoDispensadora=null;
		this.medicamento=null;
		this.tipoApresentacaoMedicamento=null;
		this.grupoApresMdtos=null;
		this.pacCodigo = null;
		this.prontuario = null;
		this.paciente = null;
	}

	public void processarPesquisaPaciente(){
		if(prontuario != null){
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
		}else{ 
			if(pacCodigo != null){
				paciente = pacienteFacade.obterPacientePorCodigo(pacCodigo);
			}
		}
		pacCodigo = paciente != null ? paciente.getCodigo() : null;
		prontuario = paciente != null ? paciente.getProntuario() : null;
	}
	/**	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {			
			processarPesquisaPaciente();
			colecao = this.farmaciaFacade.obterListaMedicamentosDispensadosPorBox(
					getDataEmissaoInicio(), 
					getDataEmissaoFim(),
					getEstacaoDispensadora(),
					getMedicamento(),
					getTipoApresentacaoMedicamento(),
					getGrupoApresMdtos(), pacCodigo);

			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}
			DocumentoJasper documento = gerarDocumento();
			/*print(false);
			cupsFacade.enviarPdfCupsPorClasse(
					getArquivoGerado(),
					DominioNomeRelatorio.MEDICAMENTO_DISPENSADO_POR_BOX,
					super.getEnderecoRedeHostRemoto());*/
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	

	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 */
	public String print()throws JRException, IOException, DocumentException{
		try {

			colecao = this.farmaciaFacade.obterListaMedicamentosDispensadosPorBox(
					getDataEmissaoInicio(), 
					getDataEmissaoFim(),
					getEstacaoDispensadora(),
					getMedicamento(),
					getTipoApresentacaoMedicamento(),
					getGrupoApresMdtos(), pacCodigo);
					

			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}		
			return PAGE_RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar(){
		return PAGE_RELATORIO_MEDICAMENTOS_DISPENSADOS_POR_BOX;
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 */
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/medicamentoDispensadoPorBox.jasper";
	}
	
	@Override
	public Collection<MedicamentoDispensadoPorBoxVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));
		params.put("dataEmissaoInicio", DateUtil.obterDataFormatada(dataEmissaoInicio, "dd/MM/yyyy"));
		params.put("dataEmissaoFim", DateUtil.obterDataFormatada(dataEmissaoFim, "dd/MM/yyyy"));
		params.put("funcionalidade", "Medicamentos Dispensados Por Box");
		params.put("nomeRelatorio", "AFAR_MDTO_DISP_EST");
		if(paciente != null){
			params.put("paciente", paciente.getDsSuggestion());
		}	

		return params;			
	}
	
	public String redirecionarPesquisaFonetica() {
		paciente = null;
		return "paciente-pesquisaPacienteComponente";
	}
	
	//Getters & Setters

	public Date getDataEmissaoInicio() {
		return dataEmissaoInicio;
	}

	public void setDataEmissaoInicio(Date dataEmissaoInicio) {
		this.dataEmissaoInicio = dataEmissaoInicio;
	}

	public Date getDataEmissaoFim() {
		return dataEmissaoFim;
	}

	public void setDataEmissaoFim(Date dataEmissaoFim) {
		this.dataEmissaoFim = dataEmissaoFim;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public AfaTipoApresentacaoMedicamento getTipoApresentacaoMedicamento() {
		return tipoApresentacaoMedicamento;
	}

	public void setTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento) {
		this.tipoApresentacaoMedicamento = tipoApresentacaoMedicamento;
	}

	public AfaGrupoApresMdtos getGrupoApresMdtos() {
		return grupoApresMdtos;
	}

	public void setGrupoApresMdtos(AfaGrupoApresMdtos grupoApresMdtos) {
		this.grupoApresMdtos = grupoApresMdtos;
	}

	public AghMicrocomputador getEstacaoDispensadora() {
		return estacaoDispensadora;
	}

	public void setEstacaoDispensadora(AghMicrocomputador estacaoDispensadora) {
		this.estacaoDispensadora = estacaoDispensadora;
	}

	public List<MedicamentoDispensadoPorBoxVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<MedicamentoDispensadoPorBoxVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
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
}