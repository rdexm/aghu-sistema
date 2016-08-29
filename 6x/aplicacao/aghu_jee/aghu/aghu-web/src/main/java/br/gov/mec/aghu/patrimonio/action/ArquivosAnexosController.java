package br.gov.mec.aghu.patrimonio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaFiltroVO;
import br.gov.mec.aghu.patrimonio.vo.ArquivosAnexosPesquisaGridVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ArquivosAnexosController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4317044114744962555L;
	
	private static final Log LOG = LogFactory.getLog(ArquivosAnexosController.class);

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	private ItemRecebimentoVO itemRecebimento;
	
	private ArquivosAnexosPesquisaGridVO itemSelecionado;
	
	private ArquivosAnexosPesquisaFiltroVO arquivosAnexosPesquisaFiltroVO;
	
	private Integer indice;
	
	@Inject	@Paginator
	private DynamicDataModel<ArquivosAnexosPesquisaGridVO> dataModel;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
		
		arquivosAnexosPesquisaFiltroVO = new ArquivosAnexosPesquisaFiltroVO();
		
		this.dataModel.reiniciarPaginator();
		
	}
	
	public void carregarArquivosAnexos() {
		if (indice.equals(0)){
			iniciar();
		}else{			
			this.dataModel.limparPesquisa();
		}
	}
	
	
	@Override
	public List<ArquivosAnexosPesquisaGridVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (itemRecebimento != null){
			return this.patrimonioFacade.pesquisarArquivoAnexosPorRecebimento(arquivosAnexosPesquisaFiltroVO,itemRecebimento.getIrpSeq(), firstResult,maxResult, orderProperty, asc);			
		}
		return null;
	}

	@Override
	public Long recuperarCount() {
		if (itemRecebimento != null){
			return this.patrimonioFacade.pesquisarArquivoAnexosPorRecebimentoCount(arquivosAnexosPesquisaFiltroVO, this.itemRecebimento.getIrpSeq());			
		}
		return null;
	}
	
	public String download(Object seq){
		PtmArquivosAnexos doc = this.patrimonioFacade.obterDocumentoAnexado(Long.valueOf(seq.toString()));
		return this.downloadViaHttpServletResponse(doc);
	}	
	
	/**
	 * Download via uma Http Response
	 * 
	 * @param dados
	 *            array de bytes (stream) do arquivo de download
	 * @return
	 */
	private String downloadViaHttpServletResponse(PtmArquivosAnexos arquivosAnexos) {

		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");

		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\"" + arquivosAnexos.getArquivo() + "\"");

		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(arquivosAnexos.getAnexo()); // Escrevemos o STREAM de resposta/RESPONSE
			os.flush();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}
	
	
	public String obterHintArquivo(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if(item!=null){
			if (StringUtils.isNotBlank(item.getArquivo())) {
				str.append("Arquivo:").append(item.getArquivo());
			}
			if (StringUtils.isNotBlank(item.getDescricao())) {
				str.append("<br/>Descrição:").append(item.getDescricao());	
			}
		}
		return str.toString();
	}
	
	public String obterHintTipoProcesso(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (item.getTipoProcesso()!=null) {
			str.append("Tipo Processo: ").append(DominioTipoProcessoPatrimonio.getDescricao(item.getTipoProcesso()));
		}
		return str.toString();
	}
	
	public String obterTipoProcesso(String i){
		return DominioTipoProcessoPatrimonio.getDescricao(Integer.parseInt(i));
	}
	
	public String obterDescricaoResumida(Integer matricula,Short vinculo,String nome, int tamanho) {
		if (matricula != null && vinculo != null){
			return StringUtils.abbreviate(matricula + " - " + vinculo + " - " + nome, tamanho);			
		}else{
			return StringUtils.EMPTY;
		}
	}
	
	
	public String obterDataFormatada(Object objeto,String tipo){
		ArquivosAnexosPesquisaGridVO linha  = (ArquivosAnexosPesquisaGridVO) objeto;
		StringBuilder data = new StringBuilder(100);
		switch (tipo){
		case "A":{
			//ORACLE NAO OBTEM DATA COM HORA NO SQL NATIVO
			if(linha.getHoraAlt()!=null){
				data.append("Data última atualização: ").append(linha.getHoraAlt());
			}else{
				data.append("Data última atualização: ");
				data.append(DateUtil.obterDataFormatada(linha.getDtUltAltera(), "dd/MM/yyyy HH:mm:ss"));				 
			}
			
			break;
		}
		case "C":{
			if(linha.getHoraInc()!=null){
				data.append("Data Criação: ").append(linha.getHoraInc());
			}else{
				data.append("Data Criação: ");
				data.append(DateUtil.obterDataFormatada(linha.getDtCriadoEm(), "dd/MM/yyyy HH:mm:ss"));
			}
			break;
		}
		case "AS" :
		{
			if(linha.getDtUltAltera()!=null){				
				data.append(DateUtil.obterDataFormatada(linha.getDtUltAltera(), "dd/MM/yyyy")).append("...");
			}
			break;
		}
		case "CS":{
			if(linha.getDtCriadoEm()!=null){				
				data.append(DateUtil.obterDataFormatada(linha.getDtCriadoEm(), "dd/MM/yyyy")).append("...");	
			}
			break;
		}
		default: 
			data.append("");
			break;
		}
		return data.toString();
	}
	
	public String obterHintUsuarioInc(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (StringUtils.isNotBlank(item.getNome())) {
			str.append("Nome: ").append(item.getNome());
			str.append("\nMatrícula: ").append(item.getMatricula());
			str.append("\nVínculo: ").append(item.getVinCodigo());
		}
		return str.toString();
	}
	
	public String obterHintUsuarioAlt(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (StringUtils.isNotBlank(item.getNomeAlteracao())) {
			str.append("Nome: ").append(item.getNomeAlteracao());
			str.append("\nMatrícula: ").append(item.getMatriculaAlteracao());
			str.append("\nVínculo: ").append(item.getVinCodigoAlteracao());
		}
		return str.toString();
	}
	
	public String obterHintTipoDocumento(ArquivosAnexosPesquisaGridVO item) {
		StringBuilder str = new StringBuilder(100);
		if (item.getTipoDocumento()!=null) {
			str.append("Tipo Documento: ").append(DominioTipoDocumentoPatrimonio.getDescricao(item.getTipoDocumento()));
		}
		return str.toString();
	}
	
	public String obterTipoDocumento(String i){
		return DominioTipoDocumentoPatrimonio.getDescricao(Integer.parseInt(i));
	}

	public ItemRecebimentoVO getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(ItemRecebimentoVO itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public ArquivosAnexosPesquisaGridVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ArquivosAnexosPesquisaGridVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ArquivosAnexosPesquisaFiltroVO getArquivosAnexosPesquisaFiltroVO() {
		return arquivosAnexosPesquisaFiltroVO;
	}

	public void setArquivosAnexosPesquisaFiltroVO(
			ArquivosAnexosPesquisaFiltroVO arquivosAnexosPesquisaFiltroVO) {
		this.arquivosAnexosPesquisaFiltroVO = arquivosAnexosPesquisaFiltroVO;
	}

	public DynamicDataModel<ArquivosAnexosPesquisaGridVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ArquivosAnexosPesquisaGridVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getIndice() {
		return indice;
	}

	public void setIndice(Integer indice) {
		this.indice = indice;
	}
}
