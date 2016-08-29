package br.gov.mec.aghu.exames.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.CartaRecoletaVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;


public class EmitirCartaRecoletaController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(EmitirCartaRecoletaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -6384845357208890049L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IExamesFacade examesFacade;

	private Short iseSeqp;
	private Integer iseSoeSeq;
	private Short seqp;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<CartaRecoletaVO> colecao;

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/cartaRecoleta.jasper";
	}

	@Override
	public Collection<CartaRecoletaVO> recuperarColecao() {
		
			this.colecao = examesFacade.obterCartaParaImpressao(iseSeqp, iseSoeSeq, seqp);
		
		return colecao;
	}

	public void directPrint() {

		try {
			DocumentoJasper documento = gerarDocumento();
		
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
		
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
		
	}
	
	/*
	 * Método responsável por retornar os parâmetros utilizados no relatório.
	 * 
	 * @return o relatorio Jasper.
	 * @throws SystemException
	 */
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("imagemLogoHospital", this.recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		return params;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}

	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
}
