package br.gov.mec.aghu.patrimonio.cadastroapoio;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.PtmArquivosAnexos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoArquivosAnexosVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Controller da estoria 44713 - Sistema de Anexação de arquivos para o patrimônio (imagem 3)'.
 */
public class AnexosArquivosDetalhesController extends ActionController {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2140442940971790652L;
	private static final Log LOG = LogFactory.getLog(AnexosArquivosDetalhesController.class);
	private static final String anexosListPage = "patrimonio-anexosArquivosList";
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private DetalhamentoArquivosAnexosVO detalhamentoVO = new DetalhamentoArquivosAnexosVO();
	private PtmArquivosAnexos ptmArquivosAnexos;
	
	private DominioTipoProcessoPatrimonio procPatrimonio;
	private DominioTipoDocumentoPatrimonio docPatrimonio;
	private ScoMaterial material;
	private Long aaSeq;
	private String tipo;
	private String descricaoCompleta;
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		
	}
	
	public void iniciar(){
	
		if(aaSeq != null){
			
			detalhamentoVO = patrimonioFacade.obterVisualizacaoDetalhamento(aaSeq, tipo);
		}
		
		ptmArquivosAnexos = patrimonioFacade.obterArquivosAnexosPorId(aaSeq);
		docPatrimonio = ptmArquivosAnexos.getTipoDocumento();
		procPatrimonio = ptmArquivosAnexos.getTipoProcesso();
		Integer matCodigo = detalhamentoVO.getCodigoMat();
		if(matCodigo != null){
			material = estoqueFacade.obterMaterialPorId(detalhamentoVO.getCodigoMat());	
		}
		
		
	}
	
	public String cancelar(){
		return anexosListPage;
		
	}
	
	
	public String obterNomeMaterial(){
		StringBuilder sb = new StringBuilder();
		if(this.material!=null){
			sb.append(this.material.getCodigo().toString());
			if(this.material.getNome() != null){
				sb.append(" - ").append(this.material.getNome());
			}
			return sb.toString();
		}
		return null;
	}
	
	public String obterHintMaterial(){
		String hint ="";
		if(material!=null){
		 hint = material.getNome();
		}
		return hint;
	}
	public void download(){
		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");

		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\"" + detalhamentoVO.getArquivoAA() + "\"");

		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(ptmArquivosAnexos.getAnexo()); // Escrevemos o STREAM de resposta/RESPONSE
			os.flush();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}  
	}
	

	public String obterDataFormatadaCriacao(){
		if(detalhamentoVO.getHoraInc()!=null){
				return detalhamentoVO.getHoraInc();
			}else{
				return DateUtil.obterDataFormatada(detalhamentoVO.getCriadoEmAA(), "dd/MM/yyyy HH:mm:ss");
			}
			
	}
	public String obterDataFormatadaAlteracao(){
		if(detalhamentoVO.getHoraAlt()!=null){
				return detalhamentoVO.getHoraAlt();
		}else{
				return DateUtil.obterDataFormatada(detalhamentoVO.getAlteradoEmAA(), "dd/MM/yyyy HH:mm:ss");
		}
	}
	
	public String obterTipoProcesso(){
		if(detalhamentoVO!=null){
			return DominioTipoProcessoPatrimonio.getDescricao(detalhamentoVO.getTipoProcesso());
		}
		else{
			return "";
		}
	}
	public String obterTipoDocumento(){
		if(detalhamentoVO!=null){
			return DominioTipoDocumentoPatrimonio.getDescricao(detalhamentoVO.getTipoDocumento());	
		}
		else{
			return "";
		}
	}
	
    public Long getAaSeq() {
		return aaSeq;
	}


	public DetalhamentoArquivosAnexosVO getDetalhamentoVO() {
		return detalhamentoVO;
	}

	public void setDetalhamentoVO(DetalhamentoArquivosAnexosVO detalhamentoVO) {
		this.detalhamentoVO = detalhamentoVO;
	}

	public void setAaSeq(Long aaSeq) {
		this.aaSeq = aaSeq;
	}

	public DominioTipoProcessoPatrimonio getProcPatrimonio() {
		return procPatrimonio;
	}

	public void setProcPatrimonio(DominioTipoProcessoPatrimonio procPatrimonio) {
		this.procPatrimonio = procPatrimonio;
	}

	public DominioTipoDocumentoPatrimonio getDocPatrimonio() {
		return docPatrimonio;
	}

	public void setDocPatrimonio(DominioTipoDocumentoPatrimonio docPatrimonio) {
		this.docPatrimonio = docPatrimonio;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public String getDescricaoCompleta() {
		return descricaoCompleta;
	}

	public void setDescricaoCompleta(String descricaoCompleta) {
		this.descricaoCompleta = descricaoCompleta;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
}
