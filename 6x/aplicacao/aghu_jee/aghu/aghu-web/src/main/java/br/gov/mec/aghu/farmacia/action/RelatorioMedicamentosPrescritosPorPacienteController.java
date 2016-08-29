package br.gov.mec.aghu.farmacia.action;

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

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.MedicamentoPrescritoPacienteVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import net.sf.jasperreports.engine.JRException;

public class RelatorioMedicamentosPrescritosPorPacienteController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 7566254150533411410L;
	
	private static final String PAGE_RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE= "relatorioMedicamentosPrescritosPorPaciente";
	private static final String PAGE_RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE_PDF= "relatorioMedicamentosPrescritosPorPacientePdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataDeReferenciaInicio;
	private Date dataDeReferenciaFim;
	private AfaMedicamento medicamento;
	private AfaGrupoUsoMedicamento grupo;
	private AfaTipoUsoMdto tipo;
	private Boolean itemDispensado = Boolean.TRUE;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	private Integer codPaciente;
	private Integer prontuario;
	private AipPacientes paciente;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void iniciarPagina(){	
		
		CodPacienteFoneticaVO codPacienteFonetica = this.codPacienteFonetica.get(); 
		
		if(codPacienteFonetica != null && codPacienteFonetica.getCodigo() > 0){
			codPaciente = codPacienteFonetica.getCodigo();
		}
		
		if (codPaciente != null) {
			paciente = pacienteFacade.obterPacientePorCodigo(codPaciente);
			prontuario = paciente.getProntuario();
		}
		
	} 

	public String redirecionarPesquisaFonetica() {
		return "paciente-pesquisaPacienteComponente";
	}
	
	/**
	 * Dados que serão impressos 
	 */
	private List<MedicamentoPrescritoPacienteVO> colecao = new ArrayList<MedicamentoPrescritoPacienteVO>(0);

	/**
	 * Pesquisa Unidades Funcionais conforme o parametro recebido
	 * 
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String parametro) {
		return this.returnSGWithCount(farmaciaFacade.listarUnidadesPrescricaoByPesquisa(parametro),pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(parametro));
	}
	
	/**
	 * Count de Unidades Funcionais conforme o parametro recebido
	 * 
	 */
	public Long pesquisarUnidadeFuncionalPorCodigoEDescricaoCount(String parametro) {
		return farmaciaFacade.listarUnidadesPrescricaoByPesquisaCount(parametro);
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Pesquisa Medicamentos conforme o parametro recebido
	 * 
	 */
	public List<AfaMedicamento> pesquisarMedicamentos(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.pesquisarListaMedicamentos(strPesquisa),pesquisarMedicamentosCount(strPesquisa));
	}
	
	/**
	 * Count da Pesquisa Medicamentos conforme o parametro recebido
	 * 
	 */
	public Long pesquisarMedicamentosCount(String strPesquisa){
		return farmaciaFacade.pesquisarListaMedicamentosCount(strPesquisa);
	}

	/**
	 * Pesquisa Grupo de Uso de Medicamentos conforme o parametro recebido
	 * 
	 */
	public List<AfaGrupoUsoMedicamento> pesquisarGrupoUsoMedicamento(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.pesquisarTodosGrupoUsoMedicamento(strPesquisa),pesquisarGrupoUsoMedicamentoCount(strPesquisa));
	}
	
	/**
	 * Count de Grupo de Uso de Medicamentos conforme o parametro recebido
	 * 
	 */
	public Integer pesquisarGrupoUsoMedicamentoCount(String strPesquisa){
		return farmaciaFacade.pesquisarTodosGrupoUsoMedicamentoCount(strPesquisa);
	}

	/**
	 * Pesquisa Tipo de Uso de Medicamentos conforme o parametro recebido
	 * 
	 */
	public List<AfaTipoUsoMdto> pesquisarTiposUsoMdto(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.pesquisarTodosTipoUsoMedicamento(strPesquisa),pesquisarTiposUsoMdtoCount(strPesquisa));
	}
	
	/**
	 * Count de Tipo de Uso de Medicamentos conforme o parametro recebido
	 * 
	 */
	public Integer pesquisarTiposUsoMdtoCount(String strPesquisa){
		return farmaciaFacade.pesquisarTodosTipoUsoMedicamentoCount(strPesquisa);
	}

	public void limparPesquisa() {
		this.paciente = null;
		this.codPaciente = null;
		this.prontuario = null;
		this.dataDeReferenciaInicio = null;
		this.dataDeReferenciaFim = null;
		this.unidadeFuncional = null;
		this.medicamento = null;
		this.grupo = null;
		this.tipo = null;
		this.itemDispensado = Boolean.TRUE;
	}
	
	public String voltar(){
		return PAGE_RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE;
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint()throws SistemaImpressaoException, ApplicationBusinessException{

		try {

			if (validaDatas(dataDeReferenciaInicio, dataDeReferenciaFim)){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL");
				return;
			}

			colecao = this.farmaciaFacade.obterConteudoRelatorioMedicamentosPrescritosPorPaciente(dataDeReferenciaInicio, 
					dataDeReferenciaFim, unidadeFuncional, medicamento, grupo, tipo, itemDispensado, paciente != null ? paciente.getCodigo() : null);

			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

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
		if (paciente == null) {
			if(codPaciente != null){
				paciente = pacienteFacade.obterPaciente(codPaciente);
				prontuario = paciente.getProntuario();			
			}
			if(prontuario != null){
				paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
				codPaciente = paciente.getCodigo();
			}
		}
		try {

			if (validaDatas(dataDeReferenciaInicio, dataDeReferenciaFim)){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL");
			
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}

			colecao = this.farmaciaFacade.obterConteudoRelatorioMedicamentosPrescritosPorPaciente(dataDeReferenciaInicio, 
					dataDeReferenciaFim, unidadeFuncional, medicamento, grupo, tipo, itemDispensado, paciente != null ? paciente.getCodigo() : null);

			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}
			//limpaCampos();
			return PAGE_RELATORIO_MEDICAMENTOS_PRESCRITOS_POR_PACIENTE_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public Collection<MedicamentoPrescritoPacienteVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/MedicamentoPrescritoPaciente.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));
		params.put("funcionalidade",  WebUtil.initLocalizedMessage("TITLE_RELATORIO_MEDICAMENTOS_PRESCRITOS", null));
		params.put("nomeRelatorio", "AFAR_MDTO_PRCR_PAC");
		params.put("filtroEscolhido", getFiltroSelecionado());

		return params;			
	}

	private String getFiltroSelecionado() {
		StringBuffer b = new StringBuffer();
		
		if(unidadeFuncional!=null || medicamento!=null || grupo!=null || tipo!=null){
			b.append("POR ");
		}
		
		if(unidadeFuncional != null){
			b.append("UNIDADE");
			if(medicamento != null || grupo != null || tipo != null) {
				b.append(' ');
			}
		}
		if(medicamento != null){
			b.append("MEDICAMENTO");
			if(grupo != null || tipo != null) {
				b.append(' ');
			}
		}
		if(grupo != null){
			b.append("GRUPO ").append(grupo.getSeq());
			if(tipo != null) {
				b.append(' ');
			}
		}
		if(tipo != null){
			b.append("TIPO ").append(tipo.getSigla());
		}
		
		return b.toString();
	}
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 */
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException{
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public boolean validaDatas(Date dataDeReferenciaInicio, Date dataDeReferenciaFim)  {

		if (dataDeReferenciaInicio.after(dataDeReferenciaFim)) { 
			return true;
		}
		return false;		
	}
	//Getters e Setters

	public List<MedicamentoPrescritoPacienteVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<MedicamentoPrescritoPacienteVO> colecao) {
		this.colecao = colecao;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDataDeReferenciaInicio() {
		return dataDeReferenciaInicio;
	}

	public void setDataDeReferenciaInicio(Date dataDeReferenciaInicio) {
		this.dataDeReferenciaInicio = dataDeReferenciaInicio;
	}

	public Date getDataDeReferenciaFim() {
		return dataDeReferenciaFim;
	}

	public void setDataDeReferenciaFim(Date dataDeReferenciaFim) {
		this.dataDeReferenciaFim = dataDeReferenciaFim;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public AfaGrupoUsoMedicamento getGrupo() {
		return grupo;
	}

	public void setGrupo(AfaGrupoUsoMedicamento grupo) {
		this.grupo = grupo;
	}

	public AfaTipoUsoMdto getTipo() {
		return tipo;
	}

	public void setTipo(AfaTipoUsoMdto tipo) {
		this.tipo = tipo;
	}

	public Boolean getItemDispensado() {
		return itemDispensado;
	}

	public void setItemDispensado(Boolean itemDispensado) {
		this.itemDispensado = itemDispensado;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
}