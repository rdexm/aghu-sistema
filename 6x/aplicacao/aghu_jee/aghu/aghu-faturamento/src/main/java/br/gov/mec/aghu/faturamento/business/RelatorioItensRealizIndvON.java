package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoProcedAmbDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemEspelhoContaApacDAO;
import br.gov.mec.aghu.faturamento.vo.ItensRealizadosIndividuaisVO;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

@Stateless
public class RelatorioItensRealizIndvON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(RelatorioItensRealizIndvON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private FatEspelhoProcedAmbDAO fatEspelhoProcedAmbDAO;
	
	@Inject
	private FatItemEspelhoContaApacDAO fatItemEspelhoContaApacDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5594215618641803125L;

	enum FaturamentoRNExceptionCode implements BusinessExceptionCode {
		INTERVALO_PROCEDIMENTOS_INVALIDO;
	}

	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";

	
	public List<ItensRealizadosIndividuaisVO> listarItensRealzIndiv(final Date dtHoraInicio, final Integer ano, final Integer mes, Long procedInicial, Long procedFinal) throws BaseException {
		if(procedInicial != null && procedFinal != null && procedFinal < procedInicial) {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.INTERVALO_PROCEDIMENTOS_INVALIDO);
		}
		
		//FORÇA UMA NOVA TRANSAÇÃO COM TIMEOUT PRÉ-DEFINIDO
		getFaturamentoFacade().commit(60*60);
		
		List<ItensRealizadosIndividuaisVO> lista = new ArrayList<ItensRealizadosIndividuaisVO>(0);
		lista.addAll(getFatEspelhoProcedAmbDAO().listarItensRealizadosIndividuais(dtHoraInicio, ano, mes, procedInicial, procedFinal));
		lista.addAll(getFatItemEspelhoContaApacDAO().listarItensRealizadosIndividuaisApacs(ano, mes, procedInicial, procedFinal));
		
		final ComparatorChain chainSorter = new ComparatorChain();
		final BeanComparator grupoSorter = new BeanComparator("grupoSeq", new NullComparator(false));
		final BeanComparator subGrupoSorter = new BeanComparator("subGrupoSeq", new NullComparator(false));
		final BeanComparator formaOrgSorter = new BeanComparator("formaOrganizacaoCodigo", new NullComparator(false));
		final BeanComparator procedimentoSorter = new BeanComparator("procedimentoHospitalarCod", new NullComparator(false));
		final BeanComparator pacienteSorter = new BeanComparator("nome", new NullComparator(false));
		final BeanComparator prontuarioSorter = new BeanComparator("prontuario", new NullComparator(false));
		chainSorter.addComparator(grupoSorter);
		chainSorter.addComparator(subGrupoSorter);
		chainSorter.addComparator(formaOrgSorter);
		chainSorter.addComparator(procedimentoSorter);
		chainSorter.addComparator(pacienteSorter);
		chainSorter.addComparator(prontuarioSorter);
		Collections.sort(lista, chainSorter);
		
		VAipEnderecoPaciente enderecoPaciente =  null;
		for(ItensRealizadosIndividuaisVO vo : lista) {
			if(vo.getProcedencia() == null) {
				enderecoPaciente = getCadastroPacienteFacade()
				.obterEndecoPaciente(vo.getCodigo());
				if(enderecoPaciente != null) {
					vo.setCidade(enderecoPaciente.getCidade());
					vo.setEstado(enderecoPaciente.getUf());
				}
			}
		}
		
		return lista;
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioItensRealzIndv(final Date dtHoraInicio, final Integer ano, final Integer mes, Long procedInicial, Long procedFinal) throws IOException, BaseException{
		
		List<ItensRealizadosIndividuaisVO> lista = this.listarItensRealzIndiv(dtHoraInicio, ano, mes, procedInicial, procedFinal);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_ITENS_INDIV.toString(), EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Período"+SEPARADOR+"Cód. Grupo"+SEPARADOR+"Descrição Grupo"+SEPARADOR+
				"Cód. Sub-Grupo"+SEPARADOR+"Descrição Sub-Grupo"+SEPARADOR+"Cód. Forma de Organização"+SEPARADOR+"Descrição Forma de Organização"+SEPARADOR+
				  "Códgigo Procedimento"+SEPARADOR+"Descrição Procedimento"+SEPARADOR+
				  "Realizado"+SEPARADOR+"Prontuário"+SEPARADOR+"Paciente"+SEPARADOR+"APAC"+SEPARADOR+"Procedência"+SEPARADOR+
				  "Quantidade"+SEPARADOR+"Valor"+SEPARADOR+"Total Quantidade" +SEPARADOR+"Total Valor\n");

		long totalQtd = 0;
		BigDecimal totalValor = BigDecimal.ZERO;
		for (final ItensRealizadosIndividuaisVO pdf : lista) {
			
			out.write(  mes+"/"+ano+ SEPARADOR +
					   (pdf.getGrupoSeq()  != null ? pdf.getGrupoSeq() : "")  + SEPARADOR +
					   (pdf.getGrupo()  != null ? pdf.getGrupo().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getSubGrupoSeq()  != null ? pdf.getSubGrupoSeq() : "")  + SEPARADOR +
					   (pdf.getSubGrupo()  != null ? pdf.getSubGrupo().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getFormaOrganizacaoCodigo() != null ? pdf.getFormaOrganizacaoCodigo() : "")  + SEPARADOR +
					   (pdf.getFormaOrganizacao()  != null ? pdf.getFormaOrganizacao().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getProcedimentoHospitalarCod() 		  != null ? pdf.getProcedimentoHospitalarCod() : "")  + SEPARADOR +
					   (pdf.getProcedimentoHospitalarDesc()  != null ? pdf.getProcedimentoHospitalarDesc().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getDthrRealz() 		  != null ? pdf.getDthrRealz() : "")  + SEPARADOR +
					   (pdf.getProntuario() 		  != null ? pdf.getProntuario() : "")  + SEPARADOR +
					   (pdf.getNome()  != null ? pdf.getNome().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getApac() 		  != null ? pdf.getApac() : "")  + SEPARADOR +
					   (pdf.getProcedencia()  != null ? pdf.getProcedencia().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getQuantidade() 	!= null ? pdf.getQuantidade() : "") + SEPARADOR +
					   (pdf.getValorProcedimento() 		  != null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getValorProcedimento().doubleValue()) : "") + SEPARADOR +
					   ("") + SEPARADOR +
					   ("") + "\n"
			         );
			totalQtd+=pdf.getQuantidade();
			totalValor = totalValor.add(pdf.getValorProcedimento());
		}
		out.write(("") + SEPARADOR +("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR +
				("") + SEPARADOR +("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR +
				totalQtd + SEPARADOR + AghuNumberFormat.formatarNumeroMoeda(totalValor.doubleValue()));

		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	public List<ItensRealizadosIndividuaisVO> listarItensRealzIndivHist(final Date dtHoraInicio, final Integer ano, final Integer mes, Long procedInicial, Long procedFinal) throws BaseException {
		if(procedInicial != null && procedFinal != null && procedFinal < procedInicial) {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.INTERVALO_PROCEDIMENTOS_INVALIDO);
		}

		//FORÇA UMA NOVA TRANSAÇÃO COM TIMEOUT PRÉ-DEFINIDO
		getFaturamentoFacade().commit(60*60);
		
		List<ItensRealizadosIndividuaisVO> lista = new ArrayList<ItensRealizadosIndividuaisVO>(0);
		lista.addAll(getFatEspelhoProcedAmbDAO().listarItensRealizadosIndividuaisEAPAC(dtHoraInicio, ano, mes, procedInicial, procedFinal));
		
		final ComparatorChain chainSorter = new ComparatorChain();
		final BeanComparator grupoSorter = new BeanComparator("grupoSeq", new NullComparator(false));
		final BeanComparator subGrupoSorter = new BeanComparator("subGrupoSeq", new NullComparator(false));
		final BeanComparator formaOrgSorter = new BeanComparator("formaOrganizacaoCodigo", new NullComparator(false));
		final BeanComparator procedimentoSorter = new BeanComparator("procedimentoHospitalarCod", new NullComparator(false));
		final BeanComparator pacienteSorter = new BeanComparator("nome", new NullComparator(false));
		final BeanComparator prontuarioSorter = new BeanComparator("prontuario", new NullComparator(false));
		chainSorter.addComparator(grupoSorter);
		chainSorter.addComparator(subGrupoSorter);
		chainSorter.addComparator(formaOrgSorter);
		chainSorter.addComparator(procedimentoSorter);
		chainSorter.addComparator(pacienteSorter);
		chainSorter.addComparator(prontuarioSorter);
		Collections.sort(lista, chainSorter);
		
		VAipEnderecoPaciente enderecoPaciente =  null;
		for(ItensRealizadosIndividuaisVO vo : lista) {
			if(vo.getProcedencia() == null) {
				enderecoPaciente = getCadastroPacienteFacade()
				.obterEndecoPaciente(vo.getCodigo());
				if(enderecoPaciente != null) {
					vo.setCidade(enderecoPaciente.getCidade());
					vo.setEstado(enderecoPaciente.getUf());
				}
			}
		}
		
		return lista;
	}

	public String gerarPDFRelatorioItensRealzIndv(byte[] bytes) throws IOException {
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_ITENS_INDIV.toString(),".pdf");
		final FileOutputStream out = new FileOutputStream(file);

		out.write(bytes);
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}
	
	public String nameHeaderDownloadPDFRelatorioItensRealzIndv(boolean hist) {
		StringBuilder fileName = new StringBuilder(DominioNomeRelatorio.FATR_ITENS_INDIV.toString());
		if (hist){
			fileName.append("_HIS");
		}
		fileName.append(".pdf");
		return fileName.toString();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioItensRealzIndvHist(final Date dtHoraInicio, final Integer ano, final Integer mes, Long procedInicial, Long procedFinal) throws IOException, BaseException{
		
		List<ItensRealizadosIndividuaisVO> lista = this.listarItensRealzIndivHist(dtHoraInicio, ano, mes, procedInicial, procedFinal);

		final File file = File.createTempFile(DominioNomeRelatorio.FATR_ITENS_INDIV.toString(), EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write("Período"+SEPARADOR+"Cód. Grupo"+SEPARADOR+"Descrição Grupo"+SEPARADOR+
				"Cód. Sub-Grupo"+SEPARADOR+"Descrição Sub-Grupo"+SEPARADOR+"Cód. Forma de Organização"+SEPARADOR+"Descrição Forma de Organização"+SEPARADOR+
				  "Códgigo Procedimento"+SEPARADOR+"Descrição Procedimento"+SEPARADOR+
				  "Realizado"+SEPARADOR+"Prontuário"+SEPARADOR+"Paciente"+SEPARADOR+"APAC"+SEPARADOR+"Procedência"+SEPARADOR+
				  "Quantidade"+SEPARADOR+"Valor"+SEPARADOR+"Total Quantidade" +SEPARADOR+"Total Valor\n");

		long totalQtd = 0;
		BigDecimal totalValor = BigDecimal.ZERO;
		for (final ItensRealizadosIndividuaisVO pdf : lista) {
			
			out.write(  mes+"/"+ano+ SEPARADOR +
					   (pdf.getGrupoSeq()  != null ? pdf.getGrupoSeq() : "")  + SEPARADOR +
					   (pdf.getGrupo()  != null ? pdf.getGrupo().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getSubGrupoSeq()  != null ? pdf.getSubGrupoSeq() : "")  + SEPARADOR +
					   (pdf.getSubGrupo()  != null ? pdf.getSubGrupo().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getFormaOrganizacaoCodigo() != null ? pdf.getFormaOrganizacaoCodigo() : "")  + SEPARADOR +
					   (pdf.getFormaOrganizacao()  != null ? pdf.getFormaOrganizacao().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getProcedimentoHospitalarCod() 		  != null ? pdf.getProcedimentoHospitalarCod() : "")  + SEPARADOR +
					   (pdf.getProcedimentoHospitalarDesc()  != null ? pdf.getProcedimentoHospitalarDesc().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getDthrRealz() 		  != null ? pdf.getDthrRealz() : "")  + SEPARADOR +
					   (pdf.getProntuario() 		  != null ? pdf.getProntuario() : "")  + SEPARADOR +
					   (pdf.getNome()  != null ? pdf.getNome().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getApac() 		  != null ? pdf.getApac() : "")  + SEPARADOR +
					   (pdf.getProcedencia()  != null ? pdf.getProcedencia().replaceAll(";", ",") : "")  + SEPARADOR +
					   (pdf.getQuantidade() 	!= null ? pdf.getQuantidade() : "") + SEPARADOR +
					   (pdf.getValorProcedimento() 		  != null ? AghuNumberFormat.formatarNumeroMoeda(pdf.getValorProcedimento().doubleValue()) : "") + SEPARADOR +
					   ("") + SEPARADOR +
					   ("") + "\n"
					   
			         );
			totalQtd+=pdf.getQuantidade();
			totalValor = totalValor.add(pdf.getValorProcedimento());
		}
		out.write(("") + SEPARADOR +("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR +
				("") + SEPARADOR +("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR + ("") + SEPARADOR +
				totalQtd + SEPARADOR + AghuNumberFormat.formatarNumeroMoeda(totalValor.doubleValue()));
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	protected FatEspelhoProcedAmbDAO getFatEspelhoProcedAmbDAO() {
		return fatEspelhoProcedAmbDAO;
	}

	protected FatItemEspelhoContaApacDAO getFatItemEspelhoContaApacDAO() {
		return fatItemEspelhoContaApacDAO;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

}
