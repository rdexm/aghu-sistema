package br.gov.mec.aghu.patrimonio.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.RelSumarizadoNotifTecRecebProvVO;
import br.gov.mec.aghu.patrimonio.vo.RelatorioRecebimentoProvisorioVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioNotifTecRecebimentoProvisorioController extends ActionReport {

	private static final long serialVersionUID = -5978087326444659627L;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private final String PAGINA_LISTAR_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";
	
	private static final String PAGINA_VISUALIZAR_RELATORIO_NOTIFICACOES_TECNICAS = "patrimonio-visualizarRelatorioNotificacoesTecnicas";
	
	private final String CAMINHO_RELATORIO = "br/gov/mec/aghu/patrimonio/report/relatorioNotifTecRecebimentoProvisorio.jasper";
	
	private StreamedContent media;
	
	private RelSumarizadoNotifTecRecebProvVO voRelatorio;
	
	private String paginaRetorno;
	
	private Integer recebimento;
	
	private Integer itemRecebimento;

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}
	
	public String visualizarImpressao() throws ApplicationBusinessException {
		Collection<RelSumarizadoNotifTecRecebProvVO> listaRelatorio = recuperarColecao();
		
		if (listaRelatorio == null) {
			apresentarMsgNegocio(Severity.ERROR, "MS03_48204");
			return null;
		}

		return PAGINA_VISUALIZAR_RELATORIO_NOTIFICACOES_TECNICAS;
	}
	
	@Override
	protected Collection<RelSumarizadoNotifTecRecebProvVO> recuperarColecao() throws ApplicationBusinessException {
		List<RelatorioRecebimentoProvisorioVO> listaNotificacoesTecnicas = patrimonioFacade.obterRecebimentosComAF(recebimento, itemRecebimento);
		if (listaNotificacoesTecnicas == null || listaNotificacoesTecnicas.isEmpty()) {
			listaNotificacoesTecnicas = patrimonioFacade.obterRecebimentosESL(recebimento, itemRecebimento);
		}
				
		voRelatorio = new RelSumarizadoNotifTecRecebProvVO();
		
		if (listaNotificacoesTecnicas != null && !listaNotificacoesTecnicas.isEmpty()) {
			voRelatorio.setEsl(listaNotificacoesTecnicas.get(0).getEslSeq());
			voRelatorio.setRazaoSocial(listaNotificacoesTecnicas.get(0).getRazaoSocial());
			voRelatorio.setNotaFiscal(listaNotificacoesTecnicas.get(0).getDfeNumero());
			voRelatorio.setAreaTenicacAvaliacao(listaNotificacoesTecnicas.get(0).getNomeAreaTecAvaliacao());
			voRelatorio.setCentroCusto(listaNotificacoesTecnicas.get(0).getCctDescricao());
			voRelatorio.setNroCC(listaNotificacoesTecnicas.get(0).getCodCentroCusto());
			
			if (listaNotificacoesTecnicas.get(0).getIrpQuantidade() != null) {
				voRelatorio.setQuantidade(listaNotificacoesTecnicas.get(0).getIrpQuantidade());
			} else {
				voRelatorio.setQuantidade(0);
			}
			
			if (listaNotificacoesTecnicas.get(0).getCgc() != null) {
				voRelatorio.setCpfCnpj(CoreUtil.formatarCNPJ(listaNotificacoesTecnicas.get(0).getCgc()));
			} else if (listaNotificacoesTecnicas.get(0).getCpf() != null) {
				voRelatorio.setCpfCnpj(CoreUtil.formataCPF(listaNotificacoesTecnicas.get(0).getCpf()));
			} 
			
			formatarRecebimentoItem(listaNotificacoesTecnicas);
			formatarAfComplemento(listaNotificacoesTecnicas);
			calcularQuantidadeRet(listaNotificacoesTecnicas);
			formatarCodigoNomeMaterial(listaNotificacoesTecnicas);
			voRelatorio.setListaNotificacoesTecnicas(listaNotificacoesTecnicas);
		} else {
			return null;
		}

		List<RelSumarizadoNotifTecRecebProvVO> listaRelatorio = new ArrayList<RelSumarizadoNotifTecRecebProvVO>();
		listaRelatorio.add(voRelatorio);
		return listaRelatorio;
	}

	@Override
	protected String recuperarArquivoRelatorio() {		
		return CAMINHO_RELATORIO;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			parametros.put("caminhoLogo", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_JEE7));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return parametros;		
	}
	
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}
	
	public void imprimir() throws UnknownHostException, JRException {
		try {		
			DocumentoJasper documento = gerarDocumento();					
			sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch(SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public String voltar() {
		
		if (paginaRetorno == null) {		
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		} else {
			String retorno = paginaRetorno;
			paginaRetorno = null;
			return retorno;
		}
	}
	
	private void formatarRecebimentoItem(List<RelatorioRecebimentoProvisorioVO> lista) {
		voRelatorio.setRecebimentoItemFormatado(lista.get(0).getNrpSeq().toString().concat("/").concat(lista.get(0).getNroItem().toString())); 
	}
	
	private void formatarAfComplemento(List<RelatorioRecebimentoProvisorioVO> lista) {
		voRelatorio.setAfComplementoFormatado(lista.get(0).getPfrLctNumero().toString().concat("/").concat(lista.get(0).getNroComplemento().toString()));
	}
	
	private void formatarCodigoNomeMaterial(List<RelatorioRecebimentoProvisorioVO> lista) {
		voRelatorio.setCodigoNomeMaterialFormatado(lista.get(0).getMatCodigo().toString().concat(" - ").concat(lista.get(0).getMatNome()));
	}
	
	private void calcularQuantidadeRet(List<RelatorioRecebimentoProvisorioVO> lista) {
		Integer quantidade = lista.get(0).getIrpQuantidade();
		Long quantidadeDisp = lista.get(0).getPirpQuantidadeDisp();
		
		if (quantidade == null) {
			quantidade = 0;
		}		
		if (quantidadeDisp == null) {
			quantidadeDisp = 0L;
		}
		
		voRelatorio.setQuantidadeRet(quantidade.intValue() - quantidadeDisp.intValue());		
	}
	
	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public Integer getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(Integer recebimento) {
		this.recebimento = recebimento;
	}

	public Integer getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public String getPaginaRetorno() {
		return paginaRetorno;
	}

	public void setPaginaRetorno(String paginaRetorno) {
		this.paginaRetorno = paginaRetorno;
	}

}
