package br.gov.mec.aghu.faturamento.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatEspelhoEncerramentoPreviaVO;
import br.gov.mec.aghu.faturamento.vo.FatMotivoRejeicaoContasVO;
import br.gov.mec.aghu.faturamento.vo.ProcedimentoRealizadoDadosOPMVO;
import br.gov.mec.aghu.faturamento.vo.RelatorioImpressaoEspelhoContaVO;
import br.gov.mec.aghu.faturamento.vo.ValoresPreviaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.FatModalidadeAtendimento;
import br.gov.mec.aghu.model.FatMotivoCobrancaApac;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioImpressaoEspelhoContaController extends ActionReport {

	private static final long serialVersionUID = -3835122361738282290L;

	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final String URL_DOCUMENTO_JASPER = "br/gov/mec/aghu/faturamento/report/relatorioImpressaoEspelhoConta.jasper";
	private static final String URL_TELA_PESQUISA = "faturamento-encerramentoPreviaConta";
	private static final String URL_TELA_PDF = "faturamento-imprimirEspelho";
	
	private String origem;
	private StreamedContent media;
	private String param;
	
	private List<RelatorioImpressaoEspelhoContaVO> colecaoRelatorio;
	private Integer cthSeq;
	
	private FatEspelhoEncerramentoPreviaVO fat;
	
	RelatorioImpressaoEspelhoContaVO relatorioImpressaoEspelhoContaVO = new RelatorioImpressaoEspelhoContaVO();
	
	AghParametros parametro = new AghParametros();
	List<AghParametroVO> listaParametros;
	
	public String voltar() {
		return origem;
	}
	
	public String visualizarImpressao() throws ApplicationBusinessException, JRException, IOException, DocumentException{
		try{
			this.colecaoRelatorio = recuperarColecao();
			if (this.colecaoRelatorio == null || this.colecaoRelatorio.isEmpty()) {
				apresentarMsgNegocio(Severity.WARN, "NENHUM_REGISTRO_ENCONTRADO");
				return null;
			} else {
				DocumentoJasper documento = gerarDocumento();
				this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			}
			return URL_TELA_PDF;
		}catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			return URL_TELA_PESQUISA;
		}
	}
	
	protected void ajustarCnsMedicos(FatEspelhoEncerramentoPreviaVO vo) {
		/**
		 * MELHORIA #47944
		 */
		try {
			if(vo.getCpfMedicoAuditor() != null) {
				vo.setCnsMedicoAuditor(faturamentoFacade.getCnsResp(vo.getCpfMedicoAuditor()));	
			}
			if(vo.getCpfMedicoResponsavel() != null) {
				vo.setCnsMedicoResponsavel(faturamentoFacade.getCnsResp(vo.getCpfMedicoResponsavel()));	
			}
			if(vo.getCpfMedicoSolic() != null) {
				vo.setCnsMedicoSolic(faturamentoFacade.getCnsResp(vo.getCpfMedicoSolic()));	
			}
			if(vo.getCpfDoutor() != null) {
				vo.setCnsDoutor(faturamentoFacade.getCnsResp(Long.valueOf(vo.getCpfDoutor())));	
			}
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void auxilioRecuperarColecao(){
		
		/**
		 * C1
		 */
		fat = this.faturamentoFacade.obterFatEspelhoEncerramentoPreviaVO(this.cthSeq);
		
		if (fat != null) {
			Short retornoF1 = this.faturamentoFacade.fatcBuscaModalidade(fat.getIphSeqRealiz(), fat.getIphPhoSeqRealiz(), fat.getDataInternacao(), fat.getDataSaida());
			
			ajustarCnsMedicos(fat);
			
			/**
			 * F1, C11
			 */
			FatModalidadeAtendimento modalidadeAtendimento = faturamentoFacade.obterModalidadeAtivaPorCodigo(retornoF1);
			if (modalidadeAtendimento != null) {
				fat.setModalidade(modalidadeAtendimento.getDescricao());
			}
			
			/**
			 * F2
			 */
			if (fat.getDciCpeAno() != null){
				fat.formatarConta(this.faturamentoFacade.aghcModuloOnzeCompl(fat));
			} else {
				fat.setDciCpeAno((short) 0);
			}
			
			/**
			 * C2
			 */
			fat.setDescricaoNacionalidade(this.faturamentoFacade.obterDescricaoNacionalidadePorCodigo(fat.getNacionalidadePac().intValue()));
			
			/**
			 * C3
			 */
			if(fat.getEtniaId() != null){
				fat.setDescricaoEtnia(this.faturamentoFacade.obterDescricaoEtniaPorId(fat.getEtniaId()));
			}
			
			/**
			 * C4
			 */
			if(fat.getCidPrimario() != null){
				fat.setDescricaoCid(this.faturamentoFacade.obterDescricaoCidPorCodigo(fat.getCidPrimario()));
			}
			
			/**
			 * C5
			 */
			if(fat.getTciCodSus() != null){
				AinTiposCaraterInternacao tipoCaratIntern = this.faturamentoFacade.obterTiposCaraterInternacaoPorSeq(fat.getTciCodSus());
				if(tipoCaratIntern != null && (tipoCaratIntern.getCodSus() != null && tipoCaratIntern.getDescricao() != null)){
					fat.setCaraterAtendimento(tipoCaratIntern.getCodSus().toString().concat(" - ").concat(tipoCaratIntern.getDescricao()));
				}
			}
		}
	}
	
	public void auxilioRecuperarColecaoParteDois(){
		
		if (fat != null) {
			/**
			 * C6
			 */
			if(fat.getMotivoCobranca() != null){
				FatMotivoCobrancaApac motivoSaida = this.faturamentoFacade.obterMotivoCobrancaPorCodSus(Byte.parseByte(fat.getMotivoCobranca()));
				if(motivoSaida != null && motivoSaida.getCodigoSus() != null && motivoSaida.getDescricao() != null){
					fat.setMotivoSaida(motivoSaida.getCodigoSus().toString().concat(" - ").concat(motivoSaida.getDescricao()));
				}
			}
	
			/**
			 * C7
			 */
			List<ProcedimentoRealizadoDadosOPMVO> listaProcedimentosRealizados = this.faturamentoFacade.obterListaProcedimentoRealizados(fat.getCthSeq());
			if(listaProcedimentosRealizados != null && !listaProcedimentosRealizados.isEmpty()){
				
				parametro.setNome(AghuParametrosEnum.P_CNES_HCPA.toString());
				listaParametros = new ArrayList<AghParametroVO>();
				listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
				
				if (listaParametros != null && !listaParametros.isEmpty()) {
					param = listaParametros.get(0).getVlrNumerico().toString();
					for (ProcedimentoRealizadoDadosOPMVO procedimentoRealizadoDadosOPMVO : listaProcedimentosRealizados) {
						procedimentoRealizadoDadosOPMVO.setAgrupValor(listaParametros.get(0).getVlrNumerico().toString());
						if(procedimentoRealizadoDadosOPMVO.obterGrupo().equals("07")){
							if(procedimentoRealizadoDadosOPMVO.getCgc() != null){
								procedimentoRealizadoDadosOPMVO.setCnesCnpj(procedimentoRealizadoDadosOPMVO.getCgc().toString());
							}
						}else{
							if(listaParametros.get(0) != null && listaParametros.get(0).getVlrNumerico() != null){
								procedimentoRealizadoDadosOPMVO.setCnesCnpj(listaParametros.get(0).getVlrNumerico().toString());
							}
						}
					}
				}
			}
			
			relatorioImpressaoEspelhoContaVO.setListaProcedimentosRealizados(listaProcedimentosRealizados);
			
			/**
			 * C8
			 */
			List<ProcedimentoRealizadoDadosOPMVO> listaDadosOPM = this.faturamentoFacade.obterListaDadosOPM(fat.getCthSeq());
			relatorioImpressaoEspelhoContaVO.setListaDadosOPM(listaDadosOPM);
	
			/**
			 * C9
			 */
			try{
				AghParametros faturamentoPadrao = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
				List<ValoresPreviaVO> listaValoresPreviaVO = this.faturamentoFacade.obterValoresPreviaVO(fat.getCthSeq(), faturamentoPadrao);
				if (listaValoresPreviaVO != null && !listaValoresPreviaVO.isEmpty()) {
					Collections.sort(listaValoresPreviaVO);
				}
				relatorioImpressaoEspelhoContaVO.setListaValoresPrevia(listaValoresPreviaVO);
			}catch(BaseException e){
				this.apresentarExcecaoNegocio(e);
			}
			
			/**
			 * C10
			 */
			if(fat.getCthSeqReapresentada() != null){
				List<FatMotivoRejeicaoContasVO> listaMotivoRejeicao = new ArrayList<FatMotivoRejeicaoContasVO>();
				listaMotivoRejeicao =	this.faturamentoFacade.obterDescricaoMotivosRejeicaoContaPorSeq(fat.getCthSeqReapresentada());
				relatorioImpressaoEspelhoContaVO.setListaMotivosRejeicao(listaMotivoRejeicao);
			}
		}
	}
	
	

	@Override
	protected List<RelatorioImpressaoEspelhoContaVO> recuperarColecao() throws ApplicationBusinessException {
		
		this.colecaoRelatorio = new ArrayList<RelatorioImpressaoEspelhoContaVO>();
		
		auxilioRecuperarColecao();
		auxilioRecuperarColecaoParteDois();
		
		/**
		 * PARAMETROS
		 */
		if(fat != null){
			parametro.setNome(AghuParametrosEnum.P_ORGAO_LOC_REC.toString());
			listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);		
			
			if (listaParametros != null && !listaParametros.isEmpty()) {
				fat.setOrgaoLocalRecebedor(listaParametros.get(0).getVlrTexto());
			}	
			
			parametro.setNome(AghuParametrosEnum.P_CPF_DOUTOR.toString());
			listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
			if (listaParametros != null && !listaParametros.isEmpty()) {
				fat.setCpfDoutor(listaParametros.get(0).getVlrTexto());
			}
			
			parametro.setNome(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL.toString());
			listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
			if (listaParametros != null && !listaParametros.isEmpty()) {
				fat.setCnes(param.concat(" - ").concat(listaParametros.get(0).getVlrTexto()));
			}
			
			ajustarCnsMedicos(fat);
			/**
			 * Método para formatar atributos de FatEspelhoEncerramentoPreviaVO
			 */
			fat.formatarCampos();
			
		}
		relatorioImpressaoEspelhoContaVO.setFatEspelhoEncerramentoPreviaVO(fat);
		this.colecaoRelatorio.add(relatorioImpressaoEspelhoContaVO);
		
		return this.colecaoRelatorio;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return URL_DOCUMENTO_JASPER;
	}
	
	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public List<RelatorioImpressaoEspelhoContaVO> getColecaoRelatorio() {
		return colecaoRelatorio;
	}

	public void setColecaoRelatorio(
			List<RelatorioImpressaoEspelhoContaVO> colecaoRelatorio) {
		this.colecaoRelatorio = colecaoRelatorio;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}
	
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public RelatorioImpressaoEspelhoContaVO getRelatorioImpressaoEspelhoContaVO() {
		return relatorioImpressaoEspelhoContaVO;
	}

	public void setRelatorioImpressaoEspelhoContaVO(
			RelatorioImpressaoEspelhoContaVO relatorioImpressaoEspelhoContaVO) {
		this.relatorioImpressaoEspelhoContaVO = relatorioImpressaoEspelhoContaVO;
	}

	public FatEspelhoEncerramentoPreviaVO getFat() {
		return fat;
	}

	public void setFat(FatEspelhoEncerramentoPreviaVO fat) {
		this.fat = fat;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public AghParametros getParametro() {
		return parametro;
	}

	public void setParametro(AghParametros parametro) {
		this.parametro = parametro;
	}

	public List<AghParametroVO> getListaParametros() {
		return listaParametros;
	}

	public void setListaParametros(List<AghParametroVO> listaParametros) {
		this.listaParametros = listaParametros;
	}
}
