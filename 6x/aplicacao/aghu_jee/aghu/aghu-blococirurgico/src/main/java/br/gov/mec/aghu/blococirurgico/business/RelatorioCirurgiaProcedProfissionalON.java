package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProfDAO;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaProcedProfissionalVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioCirurgiaProcedProfissionalON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiaProcedProfissionalON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProfDescricoesDAO mbcProfDescricoesDAO;

	@Inject
	private PdtProfDAO pdtProfDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = -5381820947960968114L;
	
	private static final String DATE_PATTERN_MES_YYYY = "MMMM/yyyy";
	private static final String DATE_PATTERN_DD_MM_YYYY = "dd/MM/yyyy";
	private static final String DATE_PATTERN_DDMMYYYYHHMM = "ddMMyyyyHHmm";
	private static final String SEPARADOR = ";";
	private static final String QUEBRA_LINHA = "\n";
	private static final String ENCODE = "ISO-8859-1";
	private static final String LOCALE_LANG = "pt";
	private static final String LOCALE_COUNTRY = "BR";
	
	public enum RelatorioCirurgiaProcedProfissionalONExceptionCode implements BusinessExceptionCode {
		MBC_01366; 
	}	
	
	/**
	 * Procedure 
	 * 
	 * ORADB: GERA_ARQUIVO (form MBCF_GERA_ARQUIVOS) 
	 * 
	 * @param codigoPessoaFisica
	 * @param dthrInicio
	 * @param dthrFim
	 * @return
	 */
	private List<RelatorioCirurgiaProcedProfissionalVO> pesquisarCirurgiaProcedProfissional(Integer codigoPessoaFisica, Date dthrInicio, Date dthrFim) {
		List<RelatorioCirurgiaProcedProfissionalVO> lista = new ArrayList<RelatorioCirurgiaProcedProfissionalVO>();
		
		lista.addAll(getMbcProfDescricoesDAO().pesquisarProfPorCodigoPessoaFisicaDthrInicioEDthrFim(codigoPessoaFisica, dthrInicio, dthrFim));
		lista.addAll(getMbcProfCirurgiasDAO().pesquisarProfPorCodigoPessoaFisicaDthrInicioEDthrFim(codigoPessoaFisica, dthrInicio, dthrFim));
		lista.addAll(getPdtProfDAO().pesquisarProfPorCodigoPessoaFisicaDthrInicioEDthrFim(codigoPessoaFisica, dthrInicio, dthrFim));
		
		DateFormat dateFormatMesAno = new SimpleDateFormat(DATE_PATTERN_MES_YYYY, new Locale(LOCALE_LANG, LOCALE_COUNTRY));
		DateFormat dateFormatDiaMesAno = new SimpleDateFormat(DATE_PATTERN_DD_MM_YYYY, new Locale(LOCALE_LANG, LOCALE_COUNTRY));
		
		for (RelatorioCirurgiaProcedProfissionalVO relCirurgiaProcedProfissionalVO : lista) {
			Date data = relCirurgiaProcedProfissionalVO.getData();
			
			relCirurgiaProcedProfissionalVO.setMesAno(dateFormatMesAno.format(data));
			relCirurgiaProcedProfissionalVO.setDataDiaMesAno(dateFormatDiaMesAno.format(data));
			
			// DECODE(ppc.ind_principal,'S','Principal','Secundário') Tipo_proc,
			if (relCirurgiaProcedProfissionalVO.getIndPrincipal()) {
				relCirurgiaProcedProfissionalVO.setIndPrincipalDescricao("Principal");
			} else {
				relCirurgiaProcedProfissionalVO.setIndPrincipalDescricao("Secundário");
			}
			
			DominioTipoAtuacao tipoAtuacao = relCirurgiaProcedProfissionalVO.getTipoAtuacao();
			DominioFuncaoProfissional funcaoProfissional = relCirurgiaProcedProfissionalVO.getFuncaoProfissional();
			
			// atuacao engloba enums diferentes
			if (tipoAtuacao != null) {
				relCirurgiaProcedProfissionalVO.setAtuacao(tipoAtuacao.getDescricao());
			} else if (funcaoProfissional != null) {
				relCirurgiaProcedProfissionalVO.setAtuacao(funcaoProfissional.getDescricao());
			}
		}
		
		Set<RelatorioCirurgiaProcedProfissionalVO> setCirurgiaProcedProfissionalVOs =  new HashSet<RelatorioCirurgiaProcedProfissionalVO>();
		setCirurgiaProcedProfissionalVOs.addAll(lista);
		
		List<RelatorioCirurgiaProcedProfissionalVO> list = new ArrayList<RelatorioCirurgiaProcedProfissionalVO>(setCirurgiaProcedProfissionalVOs);
		
		ordenarLista(list);
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private void ordenarLista(List<RelatorioCirurgiaProcedProfissionalVO> lista) {
		final ComparatorChain comparatorChain = new ComparatorChain();
		final BeanComparator dataComparator = new BeanComparator(
				RelatorioCirurgiaProcedProfissionalVO.Fields.DATA.toString(), new NullComparator(false));
		final BeanComparator prontuarioComparator = new BeanComparator(
				RelatorioCirurgiaProcedProfissionalVO.Fields.PRONTUARIO.toString(), new NullComparator(false));
		final BeanComparator indPrincipalComparator = new BeanComparator(
				RelatorioCirurgiaProcedProfissionalVO.Fields.IND_PRINCIPAL.toString(), new NullComparator(false));
		final BeanComparator procedimentoComparator = new BeanComparator(
				RelatorioCirurgiaProcedProfissionalVO.Fields.PROCEDIMENTO.toString(), new NullComparator(false));	
		final BeanComparator atuacaoComparator = new BeanComparator(
				RelatorioCirurgiaProcedProfissionalVO.Fields.ATUACAO.toString(), new NullComparator(false));		
		
		comparatorChain.addComparator(dataComparator);
		comparatorChain.addComparator(prontuarioComparator);
		comparatorChain.addComparator(indPrincipalComparator, true);
		comparatorChain.addComparator(procedimentoComparator);
		comparatorChain.addComparator(atuacaoComparator);
		
		Collections.sort(lista, comparatorChain);
	}
	
	public NomeArquivoRelatorioCrgVO gerarRelatorioCirurgiaProcedProfissionalCSV(Integer codigoPessoaFisica, Date dthrInicio,
			Date dthrFim, String extensaoArquivo) throws IOException, ApplicationBusinessException {
		
		if (dthrInicio == null || dthrFim == null || extensaoArquivo == null) {
			throw new IllegalArgumentException("Os parâmetros dthrInicio, dthrFim e extensaoArquivo são obrigatórios!");
		}

		if (codigoPessoaFisica == null) {
			throw new ApplicationBusinessException(RelatorioCirurgiaProcedProfissionalONExceptionCode.MBC_01366);
		}
		
		List<RelatorioCirurgiaProcedProfissionalVO> listaCirurgiaProcedProfissional = 
				this.pesquisarCirurgiaProcedProfissional(codigoPessoaFisica, dthrInicio, dthrFim);
				
		String nomeRelatorio = DominioNomeRelatorio.MBC_PROF.toString() + "_" + 
				new SimpleDateFormat(DATE_PATTERN_DDMMYYYYHHMM, new Locale(LOCALE_LANG, LOCALE_COUNTRY)).format(new Date());
		
		final File file = File.createTempFile(nomeRelatorio, extensaoArquivo);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("MÊS/ANO" + SEPARADOR + "MATRÍCULA" + SEPARADOR + "VÍNCULO" + SEPARADOR 
				+ "DATA" + SEPARADOR + "PRONTUÁRIO" + SEPARADOR + "NOME" + SEPARADOR 
				+ "TIPO_PROC" + SEPARADOR + "PROCEDIMENTO" + SEPARADOR 
				+ "ATUAÇÃO" + SEPARADOR + "ESP" + QUEBRA_LINHA);
		
		for (RelatorioCirurgiaProcedProfissionalVO vo : listaCirurgiaProcedProfissional) {
			out.write(vo.getMesAno() + SEPARADOR + vo.getSerMatriculaProf() + SEPARADOR + vo.getSerVinCodigoProf() + SEPARADOR 
					+ vo.getDataDiaMesAno() + SEPARADOR + vo.getProntuario() + SEPARADOR + vo.getNome() + SEPARADOR 
					+ vo.getIndPrincipalDescricao() + SEPARADOR + vo.getProcedimento() + SEPARADOR 
					+ vo.getAtuacao() + SEPARADOR + vo.getSiglaEsp() + QUEBRA_LINHA);
		}
		
		out.flush();
		out.close();
		
		NomeArquivoRelatorioCrgVO vo = new NomeArquivoRelatorioCrgVO();
		vo.setFileName(file.getAbsolutePath());
		vo.setNomeRelatorio(nomeRelatorio);
		
		return vo;
	}
	
	protected MbcProfDescricoesDAO getMbcProfDescricoesDAO() {
		return mbcProfDescricoesDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected PdtProfDAO getPdtProfDAO() {
		return pdtProfDAO;
	}
}
