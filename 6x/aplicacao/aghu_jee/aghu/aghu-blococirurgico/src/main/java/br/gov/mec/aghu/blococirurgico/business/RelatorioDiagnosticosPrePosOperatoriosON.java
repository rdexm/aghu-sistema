package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.vo.DiagnosticosPrePosOperatoriosVO;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Procedure
 * 
 * @author Alex Sander Tavares Vieira
 * @ORADB GERA_ARQUIVO_CID
 */
@Stateless
public class RelatorioDiagnosticosPrePosOperatoriosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioDiagnosticosPrePosOperatoriosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	private static final long serialVersionUID = -41729140477938772L;
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
		
	public enum RelatorioDiagnosticosPrePosOperatoriosONExceptionCode implements BusinessExceptionCode {
		REL_DPPO_PARAMETROS_OBRIGATORIOS;
	}
	
	private String gerarCabecalhoDoRelatorio() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_PRONTUARIO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_CODIGO_PAC"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_NUMERO_CIRURGIA"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_MES"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_DIAGNOSTICOS"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_CID_PRE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_CID_POS"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_EQUIPE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_DPPO_ESPECIALIDADE"))
			.append(QUEBRA_LINHA);
			
		return buffer.toString();
	}
	
	private String gerarLinhasDoRelatorio(DiagnosticosPrePosOperatoriosVO linha) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(linha.getStrProntuario())
			.append(SEPARADOR)
			.append(linha.getPacCodigo())
			.append(SEPARADOR)
			.append(linha.getNumeroCirurgia())
			.append(SEPARADOR)
			.append(linha.getStrDataMesAno())
			.append(SEPARADOR)
			.append(linha.getStrDiagnostico())
			.append(SEPARADOR)
			.append(linha.getStrPre())
			.append(SEPARADOR)
			.append(linha.getStrPos())
			.append(SEPARADOR)
			.append(linha.getStrEquipe())
			.append(SEPARADOR)
			.append(linha.getNomeEspecialidade())
			.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
	
	
	private MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO() {
        return mbcDiagnosticoDescricaoDAO;
    }
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	/**
	 * Function
	 * 
	 * @ORADB RAPC_BUSCA_NOME
	 * @param matricula
	 * @param vinCodigo
	 */
	private String obterRapcBuscaNome(Integer matricula, Short vinCodigo) {
		
		RapServidores rapServidores = getRegistroColaboradorFacade()
				.obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(matricula, vinCodigo);

		if (rapServidores != null && rapServidores.getPessoaFisica() != null) {
			return rapServidores.getPessoaFisica().getNome();
		}
		return "";
	}

	/**
	 * Function
	 * 
	 * @ORADB MBCC_RET_DIAG_DESC
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 */
	private String obterMbccRetDiagDesc(Integer dcgCrgSeq, Short dcgSeqp){
		List<MbcDiagnosticoDescricao> diagnosticoDescricaos = getMbcDiagnosticoDescricaoDAO().obterMbccRetDiagDesc(dcgCrgSeq, dcgSeqp);

		StringBuilder classifPre = new StringBuilder();
		StringBuilder classifPos = new StringBuilder();
		
		if(!diagnosticoDescricaos.isEmpty()){
			for (MbcDiagnosticoDescricao mbcDiagnosticoDescricao : diagnosticoDescricaos) {
				if(mbcDiagnosticoDescricao.getId().getClassificacao().equals(DominioClassificacaoDiagnostico.PRE)){
					classifPre.append(DominioClassificacaoDiagnostico.PRE.toString());
				}
				
				if(mbcDiagnosticoDescricao.getId().getClassificacao().equals(DominioClassificacaoDiagnostico.POS)){
					classifPos.append(DominioClassificacaoDiagnostico.POS.toString());
				}
			}
			
			return StringUtils.substring(classifPre.toString() + classifPos.toString(), 0, 3) ;
		}else{
			return "";
		}
	}
	
	/**
	 * Function
	 * 
	 * @ORADB MBCC_DIAG_PRE_POS
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @param classificacao
	 */
	private String obterMbccDiagPrePos(final Integer dcgCrgSeq, final Short dcgSeqp, DominioClassificacaoDiagnostico classificacao){
		StringBuffer buffer = new StringBuffer();

		List<MbcDiagnosticoDescricao> diagnosticoDescricaos = getMbcDiagnosticoDescricaoDAO().obterMbccDiagPrePos(dcgCrgSeq, dcgSeqp, classificacao);
		if(!diagnosticoDescricaos.isEmpty()){
			
			final String virgulaEspaco = ", ";
			int countCid = 1;
			
			for(MbcDiagnosticoDescricao lista : diagnosticoDescricaos){
				if (countCid != diagnosticoDescricaos.size() && diagnosticoDescricaos.size() >  1){
					buffer.append(lista.getCid().getCodigo()).append(virgulaEspaco);
				}else{
					buffer.append(lista.getCid().getCodigo());
				}
				countCid ++;
			}
		}
		return buffer.toString();
	}
	
	@SuppressWarnings("unchecked")
	private void ordernarLista(List<DiagnosticosPrePosOperatoriosVO> lista) {
		final ComparatorChain comparatorChain = new ComparatorChain();
		
		final BeanComparator pacCodigoComparator = new BeanComparator(
				DiagnosticosPrePosOperatoriosVO.Fields.PAC_CODIGO.toString(), new NullComparator(false));
		final BeanComparator nroCirurgiaComparator = new BeanComparator(
				DiagnosticosPrePosOperatoriosVO.Fields.NRO_CIRURGIA.toString(), new NullComparator(false));

		comparatorChain.addComparator(pacCodigoComparator);
		comparatorChain.addComparator(nroCirurgiaComparator);
		
		Collections.sort(lista, comparatorChain);
		
	}
	
	private List<DiagnosticosPrePosOperatoriosVO> obterDiagnosticosPrePosOperatorio(final Date dataInicial, final Date dataFinal) {

		List<DiagnosticosPrePosOperatoriosVO> listVO = getMbcDiagnosticoDescricaoDAO().obterDiagnosticosPrePosOperatorio(dataInicial, dataFinal);
		
		for (DiagnosticosPrePosOperatoriosVO vo : listVO) {
			vo.setStrProntuario(String.valueOf(vo.getProntuario()));
			vo.setStrDataMesAno(DateUtil.obterDataFormatada(vo.getMes(), "MM/yyyy"));
			vo.setStrDiagnostico(obterMbccRetDiagDesc(vo.getNumeroCirurgia(), vo.getSeqp()));
			vo.setStrPre(obterMbccDiagPrePos(vo.getNumeroCirurgia(), vo.getSeqp(), DominioClassificacaoDiagnostico.PRE));
			vo.setStrPos(obterMbccDiagPrePos(vo.getNumeroCirurgia(), vo.getSeqp(), DominioClassificacaoDiagnostico.POS));
			vo.setStrEquipe(obterRapcBuscaNome(vo.getMatricula(), vo.getVinCodigo()));	
		}
		
		Set<DiagnosticosPrePosOperatoriosVO> listVODistinct =  new HashSet<DiagnosticosPrePosOperatoriosVO>();
		listVODistinct.addAll(listVO);
		
		List<DiagnosticosPrePosOperatoriosVO> list = new ArrayList<DiagnosticosPrePosOperatoriosVO>(listVODistinct);	
		
		ordernarLista(list);
		
		return list;
	}


	/**
	 * Procedure 
	 * 
	 * ORADB: GERA_ARQUIVO_CID (form MBCF_GERA_ARQUIVOS) 
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public String geraRelCSVDiagnosticosPrePosOperatorio(
			final Date dataInicial, final Date dataFinal) throws IOException, ApplicationBusinessException {
		
		if (dataInicial == null || dataFinal == null) {
			throw new ApplicationBusinessException(RelatorioDiagnosticosPrePosOperatoriosONExceptionCode.REL_DPPO_PARAMETROS_OBRIGATORIOS);
		}
		
		final List<DiagnosticosPrePosOperatoriosVO> listaLinhas = obterDiagnosticosPrePosOperatorio(dataInicial, dataFinal);

		final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_DIAGNOSTICOS_PRE_POS_OPERATORIO.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(gerarCabecalhoDoRelatorio());

		if (!listaLinhas.isEmpty()) {
			for(DiagnosticosPrePosOperatoriosVO linha : listaLinhas){
				out.write(gerarLinhasDoRelatorio(linha));
			}
		}
		
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

}
