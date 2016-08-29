package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.MbcRelatCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcTotalCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatCirurRealizPorEspecEProfON extends BaseBusiness {

	private static final long serialVersionUID = -9001235819703398140L;
	
	private static final Log LOG = LogFactory.getLog(RelatCirurRealizPorEspecEProfON.class);

	@EJB
	private BlocoCirurgicoON blocoCirurgicoON;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final String SEPARADOR=";";
	private static final String NOVA_LINHA="\n";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";

	
	protected enum RelatCirurRealizPorEspecEProfONCode implements BusinessExceptionCode {
		MBC_00497;
	}

	public String gerarLinhaPadrao(MbcRelatCirurRealizPorEspecEProfVO registro) {
		
		StringBuffer linha = new StringBuffer();
		
		linha.append(registro.getSeqEspecialidade())
		 .append(" - ")
		 .append(registro.getEspecialidade())
		 .append(SEPARADOR)
		 .append(registro.getCirurgiao())
		 .append(SEPARADOR)
		 .append(registro.getStrDataInicioCirurgia())	
		 .append(SEPARADOR)
		 .append(registro.getConvenio())
		 .append(SEPARADOR)
		 .append(registro.getNumeroAgenda())
		 .append(SEPARADOR)
		 .append(registro.getNomePaciente())
		 .append(SEPARADOR)
		 .append(registro.getProntuarioFormatado())
		 .append(SEPARADOR)
		 .append(registro.getProcedimento())	
		 .append(SEPARADOR)
		 .append(registro.getClinica())
		 .append(SEPARADOR)
		 .append(NOVA_LINHA);
		
		return linha.toString();
		
	}
	
	public String gerarLinhaParaMaisDeUmProcedimento(MbcRelatCirurRealizPorEspecEProfVO registro) {
		
		StringBuffer linha = new StringBuffer();

		for(String procedimento : registro.getProcedimentos()){
			linha.append(SEPARADOR)
				.append(SEPARADOR)
				.append(SEPARADOR)
				.append(SEPARADOR)
				.append(SEPARADOR)
				.append(SEPARADOR)
				.append(SEPARADOR)
				.append(procedimento)
				.append(SEPARADOR)
				.append(SEPARADOR)
				.append(NOVA_LINHA);
		}
		
		return linha.toString();
	}

	public String geraCabecalhoRelatCirurRealizPorEspecEProf() {
		StringBuffer cabecalho = new StringBuffer(300);		
		
		cabecalho.append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_ESPECIALIDADE"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_CIRURGIAO"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_DIA_MES_HORA"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_CONVENIO"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_AGENDA"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_PACIENTE"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_PRONTUARIO"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_PROCEDIMENTO"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_CSV_REL_CIRU_POR_ESP_CLINICA"))
			     .append(SEPARADOR)
			     .append(NOVA_LINHA); 
		
		return cabecalho.toString();
	}

		
	public String geraLinhaRelatCirurRealizPorEspecEProf(MbcRelatCirurRealizPorEspecEProfVO registro) {
		StringBuffer linha = new StringBuffer();
		
		if (registro.getIsMultiProcedimentos()) {
			linha.append(gerarLinhaPadrao(registro)).append(gerarLinhaParaMaisDeUmProcedimento(registro));
		}else{
			linha.append(gerarLinhaPadrao(registro));
		}
			
		return linha.toString();
		
	}

	public String geraCabecalhoTotRelatCirurRealizPorEspecEProf() {		
		StringBuffer cabecalho = new StringBuffer(100);
		
		cabecalho.append(getResourceBundleValue("HEADER_TOT_CSV_REL_CIRU_POR_ESP_CIRURGIAO"))
			     .append(SEPARADOR)
			     .append(getResourceBundleValue("HEADER_TOT_CSV_REL_CIRU_POR_ESP_NRO_CIRURGIAS"))
			     .append(SEPARADOR)
			     .append(NOVA_LINHA); 

		return cabecalho.toString();
	}
	
	public String geraTotalizadorRelatCirurRealizPorEspecEProf(
			MbcTotalCirurRealizPorEspecEProfVO registroTot) {
		
		StringBuffer cabecalho = new StringBuffer();
		cabecalho.append(registroTot.getCirurgiao())
			     .append(SEPARADOR)
			     .append((registroTot.getNumeroDeCirurgias() != null) ? registroTot.getNumeroDeCirurgias().intValue() : 0)
			     .append(SEPARADOR)
			     .append(NOVA_LINHA);
		
		return cabecalho.toString();
	}
	
	public String formatarProcedimentoPdf(MbcRelatCirurRealizPorEspecEProfVO vo) {
		
		StringBuffer procedimento = new StringBuffer();
		
		List<String> listaProcedimentos = getMbcCirurgiasDAO().obterProcedimentosDaCirurgia(vo.getSeqCirurgia(), Boolean.FALSE); 
		
		if (!listaProcedimentos.isEmpty()) {
			
			if (listaProcedimentos.size() > 1) {
				
				for (String str : listaProcedimentos) {
					procedimento.append(str).append(NOVA_LINHA);
				}
				
			} else {
				procedimento.append(listaProcedimentos.get(0));
			}
		}

		return procedimento.toString();
	}
	
	public String geraTotalDeCirurgiaDaEspecialidade(Integer totalDeCirurgiasPorEspecialidade){		
		
		StringBuffer linha = new StringBuffer(100);
			linha.append(getResourceBundleValue("HEADER_TOT_CSV_REL_CIRU_POR_ESP_TOTAL_CIR_ESP"))
			.append(SEPARADOR)
			.append(totalDeCirurgiasPorEspecialidade)
			.append(SEPARADOR)
			.append(NOVA_LINHA);
		return linha.toString();
	}
	
	public void formatarRelatorioPdf(final Short unidadeFuncional,
			final Date dataInicial, final Date dataFinal,
			final List<MbcRelatCirurRealizPorEspecEProfVO> lst) {
		
		for (MbcRelatCirurRealizPorEspecEProfVO vo : lst) {
			
			vo.setProntuarioFormatado(CoreUtil.formataProntuario(vo.getProntuario()));
			vo.setProcedimento(formatarProcedimentoPdf(vo));
			vo.setListaCirurgioes(getMbcCirurgiasDAO()
					.obterTotalizadorCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, vo.getSeqEspecialidade()));
		}
	}

	public void obtemTotalizadores(final Short unidadeFuncional,
			final Date dataInicial, final Date dataFinal, final Writer out,
			final Short seqEspecialidade,
			final Integer totalDeCirurgiasporEspecialidade) throws IOException {

		out.write(NOVA_LINHA);
		out.write(geraCabecalhoTotRelatCirurRealizPorEspecEProf());

		final List<MbcTotalCirurRealizPorEspecEProfVO> lstTotalizador = getMbcCirurgiasDAO()
				.obterTotalizadorCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, seqEspecialidade);

		for (MbcTotalCirurRealizPorEspecEProfVO registroTot : lstTotalizador) {
			out.write(geraTotalizadorRelatCirurRealizPorEspecEProf(registroTot));
		}

		out.write(geraTotalDeCirurgiaDaEspecialidade(totalDeCirurgiasporEspecialidade));
		out.write(NOVA_LINHA);
	}
	
	public List<MbcRelatCirurRealizPorEspecEProfVO> obterCirurRealizPorEspecEProf(
			final Short unidadeFuncional, final Date dataInicial,
			final Date dataFinal, final Short especialidade) {

		final List<MbcRelatCirurRealizPorEspecEProfVO> lst = getMbcCirurgiasDAO()
				.obterCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);

		return lst;
	}
		
	public List<MbcRelatCirurRealizPorEspecEProfVO> procDadosReportCirurRealizPorEspecEProf(
			final Short unidadeFuncional, final Date dataInicial,
			final Date dataFinal, final Short especialidade)
			throws ApplicationBusinessException {

		final List<MbcRelatCirurRealizPorEspecEProfVO> lst = obterCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);
		
		formatarRelatorioPdf(unidadeFuncional, dataInicial, dataFinal, lst);

		return lst;
	}
		
	public void formataRelatorioCSV(
			List<MbcRelatCirurRealizPorEspecEProfVO> listaVO) {

		for (MbcRelatCirurRealizPorEspecEProfVO vo : listaVO) {
			vo.setCirurgiao(StringUtils.substring(vo.getCirurgiao(), 0, 15));
			vo.setStrDataInicioCirurgia(CoreUtil.nvl(
					DateUtil.obterDataFormatada(vo.getDataInicioCirurgia(),
							"dd/MM HH:mm"), "").toString());
			vo.setProntuarioFormatado(CoreUtil.formataProntuario(vo
					.getProntuario()));
			vo.setClinica(CoreUtil.nvl(vo.getClinica(), "").toString());
			vo.setProcedimento(obterProcedimentoCSV(vo));
		}
	}

	public String geraCSVRelatCirurRealizPorEspecEProf(
			final Short unidadeFuncional, final Date dataInicial,
			final Date dataFinal, final Short especialidade)
			throws IOException, ApplicationBusinessException {
		
		final List<MbcRelatCirurRealizPorEspecEProfVO> lst = obterCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);

		formataRelatorioCSV(lst);
		
		final File file = File.createTempFile(
				DominioNomeRelatorio.RELAT_CIRUR_REALIZ_POR_ESPEC_PROF.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(geraCabecalhoRelatCirurRealizPorEspecEProf());

		if (!lst.isEmpty()) {
			
			Short seqEspAnterior = lst.get(0).getSeqEspecialidade();
			Integer crgSeqAnterior = lst.get(0).getSeqCirurgia();
			
			int totalDeCirurgiasPorEspecialidade = 1;

			for (MbcRelatCirurRealizPorEspecEProfVO vo : lst) {
				
				if (!CoreUtil.igual(crgSeqAnterior, vo.getSeqCirurgia())) {
					totalDeCirurgiasPorEspecialidade ++;
				}
				
				if (!CoreUtil.igual(seqEspAnterior, vo.getSeqEspecialidade())) {
			
					obtemTotalizadores(unidadeFuncional, dataInicial, dataFinal, out, seqEspAnterior, totalDeCirurgiasPorEspecialidade - 1);
					
					totalDeCirurgiasPorEspecialidade = 1;

					out.write(geraCabecalhoRelatCirurRealizPorEspecEProf());
				}

				crgSeqAnterior = vo.getSeqCirurgia();
				seqEspAnterior = vo.getSeqEspecialidade();

				out.write(geraLinhaRelatCirurRealizPorEspecEProf(vo));

			}

			obtemTotalizadores(unidadeFuncional, dataInicial, dataFinal, out, seqEspAnterior, totalDeCirurgiasPorEspecialidade);
		}

		out.flush();
		out.close();

		return file.getAbsolutePath();
	}
	
	
	
	
	public String obterProcedimentoCSV(MbcRelatCirurRealizPorEspecEProfVO vo) {
		StringBuffer procedimento = new StringBuffer(128365);
		List<String> listaProcedimentos = getMbcCirurgiasDAO().obterProcedimentosDaCirurgia(vo.getSeqCirurgia(),Boolean.FALSE);
		if (listaProcedimentos != null && !listaProcedimentos.isEmpty()) {
			if (listaProcedimentos.size() > 1) {
				vo.setIsMultiProcedimentos(Boolean.TRUE);
				procedimento.append(listaProcedimentos.get(0).toString());
				listaProcedimentos.remove(0);
				vo.setProcedimentos(listaProcedimentos);
			} else {
				vo.setIsMultiProcedimentos(Boolean.FALSE);
				procedimento.append(listaProcedimentos.get(0).toString());
			}
		} else {
			vo.setIsMultiProcedimentos(Boolean.FALSE);
			procedimento.append("");
		}
		return procedimento.toString();
	}
	
	public BlocoCirurgicoON getBlocoCirurgicoON() {
		return blocoCirurgicoON;
	}
	
	public void validarIntervaldoEntreDatas(final Date dataInicial, final Date dataFinal) throws ApplicationBusinessException {
		getBlocoCirurgicoON().validarIntervaldoEntreDatas(dataInicial, dataFinal, AghuParametrosEnum.P_AGHU_LIM_DT_CIR_ESP_PROF);
	}

	public MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	public void setMbcCirurgiasDAO(MbcCirurgiasDAO mbcCirurgiasDAO) {
		this.mbcCirurgiasDAO = mbcCirurgiasDAO;
	}
}