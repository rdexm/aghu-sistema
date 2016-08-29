package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioTipoEmissaoSumario;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelSumarioSessoesQuimioVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioSessoesQuimioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class SumarioQuimioPOLController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(SumarioQuimioPOLController.class);	
	private static final long serialVersionUID = -8010090307315059442L;
	
	private static final String PAGE_RELATORIO_SUMARIO="relatorioSumarioQuimio.xhtml";
	private static final String PAGE_LISTA_QUIMIO="consultarSessoesTerapeuticasQuimioPOL.xhtml";
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private Integer trpSeq;
	private List<SumarioSessoesQuimioVO> listQuinzenaQuimio;
	private List<SumarioSessoesQuimioVO> listQuinzenaQuimioSelecionada;
	private List<RelSumarioSessoesQuimioVO> colecao;
	
	private Integer pacCodigo;
	private Integer atdSeq;
	private final Integer QTDE_MAX_SELECAO_QUINZENA = 5;
	private Boolean qtdeMaxQuinzenaSelecionada = Boolean.FALSE;
	private Integer indexTabSelecionado = 0; 
	
	public enum SumarioQuimioPOLControllerExceptionCode implements	BusinessExceptionCode {
		AIP_00335 
	}
	
	@PostConstruct
	protected void inicializar(){
		 this.begin(conversation);
		 conversation.setTimeout(conversation.getTimeout()*2);
	}	
	
	public String gerarListaDatas(Integer trpSeq, Integer pacCodigo, Integer atdSeq){
		
			try {
				this.pacCodigo = pacCodigo;				
				this.atdSeq = atdSeq;
				if (this.atdSeq == null || this.atdSeq == 0){					
					throw new ApplicationBusinessException(SumarioQuimioPOLControllerExceptionCode.AIP_00335);					
				}
				gerarDadosSumarioPrescricaoQuimioPOL(atdSeq, pacCodigo);					
				
				this.trpSeq = trpSeq;
				
				listQuinzenaQuimio = prontuarioOnlineFacade.montarQuinzenaSessoesQuimio(trpSeq);
				if (listQuinzenaQuimio.size() > 1) {
					openDialog("sumarioQuimioModalWG");
					gerarListaRelatorio();
					return null;
				}
				listQuinzenaQuimioSelecionada = listQuinzenaQuimio;
				
				return PAGE_RELATORIO_SUMARIO;
				
			} catch (BaseException e) {				
				apresentarExcecaoNegocio(e);
			}
		return null;
	}
	
	private void gerarDadosSumarioPrescricaoQuimioPOL(Integer atdSeq, Integer pacCodigo) {
		try {
			prontuarioOnlineFacade.gerarDadosSumarioPrescricaoQuimioPOL(atdSeq, pacCodigo, DominioTipoEmissaoSumario.P);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}			
	}

	public String gerarVisualizacaoRelatorio(){
		gerarListaRelatorio();
		return PAGE_RELATORIO_SUMARIO;
	}

	public void gerarListaRelatorio(){
		listQuinzenaQuimioSelecionada = new ArrayList<SumarioSessoesQuimioVO>();
		indexTabSelecionado=0;
		for (SumarioSessoesQuimioVO sumarioSessoesQuimioVO : listQuinzenaQuimio) {
			if(sumarioSessoesQuimioVO.isSelected()){
				listQuinzenaQuimioSelecionada.add(sumarioSessoesQuimioVO);
			}
		}
	}
	
	public void validaSelecaoQuinzena(Integer idxQuinzena){
		Integer countQuinzenaSelect = 0;
		for (SumarioSessoesQuimioVO sumarioSessoesQuimioVO : listQuinzenaQuimio) {
			if(sumarioSessoesQuimioVO.isSelected()){
				countQuinzenaSelect++;
			}
		}
		
		if(countQuinzenaSelect > QTDE_MAX_SELECAO_QUINZENA){
			for (SumarioSessoesQuimioVO sumarioSessoesQuimioVO : listQuinzenaQuimio) {
				if(sumarioSessoesQuimioVO.getIdx() == idxQuinzena){
					sumarioSessoesQuimioVO.setSelected(Boolean.FALSE);
					qtdeMaxQuinzenaSelecionada = Boolean.TRUE;
				}
			}
		}else{
			qtdeMaxQuinzenaSelecionada = Boolean.FALSE;	
		}
	}
	
	public Boolean getQuinzenaSelecionada(){
		if(listQuinzenaQuimio == null){
			return Boolean.FALSE;
		}
		for (SumarioSessoesQuimioVO sumarioSessoesQuimioVO : listQuinzenaQuimio) {
			if(sumarioSessoesQuimioVO.isSelected()){
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
	
	public String voltar() {
		return PAGE_LISTA_QUIMIO;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioSumarioSessoesQuimio.jasper";
	}
	
	@Override
	public List<RelSumarioSessoesQuimioVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		

		return params;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException  {
		if (!listQuinzenaQuimioSelecionada.isEmpty()){
			colecao = listQuinzenaQuimioSelecionada.get(indexTabSelecionado).getColecao();
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
		}
		return null;
	}
	
	public void directPrint() {
		try {
			colecao = listQuinzenaQuimioSelecionada.get(indexTabSelecionado).getColecao();
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	
	public Integer getTrpSeq() {
		return trpSeq;
	}

	public void setTrpSeq(Integer trpSeq) {
		this.trpSeq = trpSeq;
	}

	public void setListQuinzenaQuimio(List<SumarioSessoesQuimioVO> listQuinzenaQuimio) {
		this.listQuinzenaQuimio = listQuinzenaQuimio;
	}

	public List<SumarioSessoesQuimioVO> getListQuinzenaQuimio() {
		return listQuinzenaQuimio;
	}

	public void setListQuinzenaQuimioSelecionada(
			List<SumarioSessoesQuimioVO> listQuinzenaQuimioSelecionada) {
		this.listQuinzenaQuimioSelecionada = listQuinzenaQuimioSelecionada;
	}

	public List<SumarioSessoesQuimioVO> getListQuinzenaQuimioSelecionada() {
		return listQuinzenaQuimioSelecionada;
	}

	public void setColecao(List<RelSumarioSessoesQuimioVO> colecao) {
		this.colecao = colecao;
	}

	public List<RelSumarioSessoesQuimioVO> getColecao() {
		return colecao;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Boolean getQtdeMaxQuinzenaSelecionada() {
		return qtdeMaxQuinzenaSelecionada;
	}

	public void setQtdeMaxQuinzenaSelecionada(Boolean qtdeMaxQuinzenaSelecionada) {
		this.qtdeMaxQuinzenaSelecionada = qtdeMaxQuinzenaSelecionada;
	}

	public Integer getIndexTabSelecionado() {
		return indexTabSelecionado;
	}

	public void setIndexTabSelecionado(Integer indexTabSelecionado) {
		this.indexTabSelecionado = indexTabSelecionado;
	}
}
