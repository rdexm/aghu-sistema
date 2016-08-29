package br.gov.mec.aghu.estoque.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.vo.EntradaSaidaSemLicitacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GeracaoRelatoriosExcelON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7654893968162736485L;
	
	private static final Log LOG = LogFactory.getLog(GeracaoRelatoriosExcelON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	private static final String SEPARADOR = ";";
	private static final String ENCODE = "ISO-8859-1";
	private static final String EXTENSAO = ".csv";
	private static final String FORMATO_DATA = "dd/MM/yyyy";
	private static final String FORMATO_DATA_DD_MMM_YY = "dd-MMM-yy";
	
	
	private enum GeracaoRelatoriosExcelONExceptionCode implements BusinessExceptionCode {
		CAMPO_DATA_OBRIGATORIO_ESSL, CAMPO_DATA_INICIAL_OBRIGATORIO_ESSL, CAMPO_DATA_FINAL_OBRIGATORIO_ESSL, 
		CAMPO_DATA_FINAL_MAIOR_QUE_DATA_INICIAL, CRITERIO_DE_PESQUISA_TIPO_MOVIMENTO;
	}
	
	/**
	 * #34163 Método que gera o arquivo CSV para o relatório Entrada e Saida Sem Licitacao.
	 * 
	 * @param filtroSL
	 * @return
	 * @throws IOException
	 */
	public String gerarRelatoriosExcelESSL(EntradaSaidaSemLicitacaoVO filtroSL) throws IOException, ApplicationBusinessException{
        
        List<EntradaSaidaSemLicitacaoVO> listVO = estoqueFacade.listarEntradaSaidaSemLicitacaoSL(filtroSL);
        Set<EntradaSaidaSemLicitacaoVO> setListVo = new HashSet<EntradaSaidaSemLicitacaoVO>();
        
	    if(listVO != null && listVO.isEmpty()){
	       return null;
	    }else{
	    	setListVo = new HashSet<EntradaSaidaSemLicitacaoVO>(listVO);
	    	listVO = new ArrayList<EntradaSaidaSemLicitacaoVO>();
	    	listVO.addAll(setListVo);
	    	listVO = ordenarRelatorioESSL(listVO);
	    }
                             
        final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_CSV_NRS_MARCAS_DIVERGENTES.getDescricao(), EXTENSAO);
        
        final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
        
        out.write("GR" + SEPARADOR + 
        		  "TIPO" + SEPARADOR + 
        		  "DOC" + SEPARADOR + 
        		  "DOC_ORIGEM" + SEPARADOR + 
        		  "TIPO" + SEPARADOR + 
        		  "SOLIC" + SEPARADOR + 
        		  "DATA" + SEPARADOR + 
        		  "ENC" + SEPARADOR + 
        		  "EST" + SEPARADOR + 
        		  "ALMOX" + SEPARADOR + 
        		  "MATERIAL" + SEPARADOR  + 
        		  "NOME" + SEPARADOR  +
        		  "UNID" + SEPARADOR + 
        		  "QTDE" + SEPARADOR + 
        		  "DEVOL" + SEPARADOR + 
        		  "VALOR" + SEPARADOR + 
        		  "FORNECEDOR" + SEPARADOR + 
        		  "CNPJ/CPF" + SEPARADOR + 
        		  "NRO_NF" + SEPARADOR + 
        		  "AF" + SEPARADOR + 
        		  "ITEM AF \n");
        
        for (final EntradaSaidaSemLicitacaoVO sdVO : listVO) {
               
	    	out.write(   validarObjetoNulo(sdVO.getCampo00()) + SEPARADOR 
                         + validarObjetoNulo(sdVO.getCampo01()) + SEPARADOR 
                         + validarObjetoNulo(sdVO.getCampo02()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo03()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo04()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo05()) + SEPARADOR
                         + formataData(sdVO.getCampo06()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo07()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo08()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo09()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo10()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo11()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo12()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo13()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getCampo14()) + SEPARADOR
                         + String.format("%.4f", sdVO.getCampo15()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getVRazaoSocial()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getVcnpj()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getNroNfC3()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getAfC4()) + SEPARADOR
                         + validarObjetoNulo(sdVO.getItemAFC5()) +"\n");
        }
        
        out.flush();
        out.close();

        return file.getAbsolutePath();
  }
	
	/**Ordenar lista da Relatorio CSV ESSL
	 * 
	 * @param listaESSL
	 * @return
	 */
	private List<EntradaSaidaSemLicitacaoVO> ordenarRelatorioESSL(List<EntradaSaidaSemLicitacaoVO> listaESSL){
		Comparator<EntradaSaidaSemLicitacaoVO> comparator;
		comparator = new OrdenarRelatorioESSLCampoUM();
		Collections.sort(listaESSL, comparator);
		
		Comparator<EntradaSaidaSemLicitacaoVO> comparator2;
		comparator2 = new OrdenarRelatorioESSLCampoDOIS();
		Collections.sort(listaESSL, comparator2);
		
		return listaESSL;
	}
	
	private static class OrdenarRelatorioESSLCampoUM implements Comparator<EntradaSaidaSemLicitacaoVO> {
		@Override
		public int compare(EntradaSaidaSemLicitacaoVO o1, EntradaSaidaSemLicitacaoVO o2) {
			Integer nota1=null;Integer nota2=null;
			if(o1.getCampo00()!= null){
				nota1 = (o1.getCampo00());
			}
			if(o2.getCampo00()!=null){
				nota2 = (o2.getCampo00());
			}
			return nota1 - nota2;
		}
	}
	
	private static class OrdenarRelatorioESSLCampoDOIS implements Comparator<EntradaSaidaSemLicitacaoVO> {
        @Override
        public int compare(EntradaSaidaSemLicitacaoVO o1, EntradaSaidaSemLicitacaoVO o2) {
        	if (o1.getCampo00()!= null && o2.getCampo00()!=null && o1.getCampo00().equals(o2.getCampo00())) {
        		Integer nota1=null;Integer nota2=null;
				
        		if(o1.getCampo000()!= null){
    				nota1 = (o1.getCampo000());
    			}
    			if(o2.getCampo000()!=null){
    				nota2 = (o2.getCampo000());
    			}
    			return nota1 - nota2;	
        	}
        	return 0;
        }
    }
	
	/**
	 * Valida se o objeto retornado é nulo.
	 * 
	 * @param objeto {@link Object} retornado.
	 * @return {@link String} valor do objeto.
	 */
	private Object validarObjetoNulo(Object objeto) {
		String retorno = StringUtils.EMPTY;
		
		if (objeto != null) {
			if (objeto instanceof Date) {
				retorno = DateUtil.obterDataFormatada((Date) objeto, FORMATO_DATA);
			} else if (objeto instanceof Double ) {
				retorno = AghuNumberFormat.formatarNumeroMoeda((Double) objeto);
			} else {
				retorno = String.valueOf(objeto);
			}
		}
		return retorno;
	}
	
	/**
	 * Formata data DD-MMM-YY Exemplo 01-JAN-14
	 * 
	 * @param objeto {@link Object} retornado.
	 * @return {@link String} valor do objeto.
	 */
	private Object formataData(Object data) {
		String retorno = StringUtils.EMPTY;
		
		if (retorno != null) {
			if (data instanceof Date) {
				retorno = DateUtil.obterDataFormatada((Date) data, FORMATO_DATA_DD_MMM_YY);
			} else {
				retorno = String.valueOf(data);
			}
		}
		return retorno.toUpperCase();
	}
	
	
	/**
	 * Valida Regras
	 * @param vo
	 * @return 
	 * @throws ApplicationBusinessException
	 */
	public void validaRegras(EntradaSaidaSemLicitacaoVO vo) throws ApplicationBusinessException {
		
		if (vo.getMmtDataComp() == null && vo.getDataInicial() == null && vo.getDataFinal() == null) {
			throw new ApplicationBusinessException(GeracaoRelatoriosExcelONExceptionCode.CAMPO_DATA_OBRIGATORIO_ESSL);
		}
		
		if (vo.getMmtDataComp() == null && vo.getDataInicial() == null) {
			throw new ApplicationBusinessException(GeracaoRelatoriosExcelONExceptionCode.CAMPO_DATA_INICIAL_OBRIGATORIO_ESSL);
		}
		
		if (vo.getMmtDataComp() == null && vo.getDataFinal() == null) {
			throw new ApplicationBusinessException(GeracaoRelatoriosExcelONExceptionCode.CAMPO_DATA_FINAL_OBRIGATORIO_ESSL);
		}
		
		if(DateUtil.validaDataMaior(vo.getDataInicial(), vo.getDataFinal())){
			throw new ApplicationBusinessException(GeracaoRelatoriosExcelONExceptionCode.CAMPO_DATA_FINAL_MAIOR_QUE_DATA_INICIAL);
		}
		
		if(vo.getListaTipoMovimento() != null && vo.getListaTipoMovimentoOrigem() != null){
			
			for (Short tipoMov : vo.getListaTipoMovimento()) {
				for (Short tipoMovOrigem : vo.getListaTipoMovimentoOrigem()) {
					
					if(tipoMov.equals(tipoMovOrigem)){
						throw new ApplicationBusinessException(GeracaoRelatoriosExcelONExceptionCode.CRITERIO_DE_PESQUISA_TIPO_MOVIMENTO);
					}
				}
				
			}
			
			
		}
	}
	
	
}
