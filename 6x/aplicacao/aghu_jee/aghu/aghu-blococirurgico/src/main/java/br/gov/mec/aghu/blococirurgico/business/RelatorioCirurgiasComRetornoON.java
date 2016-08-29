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

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaComRetornoVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioCirurgiasComRetornoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasComRetornoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	@EJB
	private BlocoCirurgicoON blocoCirurgicoON;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2566903779048898437L;

	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	private static final String BR = "<BR/>";
	private static final String SEPARADOR_QUEBRA_LINHA = " - ";
	private static final String QUEBRA_LINHA = "\n";
	private static final String NOVA_LINHA = "\r";
	
	public BlocoCirurgicoON getBlocoCirurgicoON() {
		return blocoCirurgicoON;
	}
	
	public void validarIntervaloDatasPesquisa(Date dataInicio, Date dataFim) throws BaseException {
		getBlocoCirurgicoON().validarIntervaldoEntreDatas(dataInicio, dataFim, AghuParametrosEnum.P_AGHU_INTERVALO_DATAS_PESQUISA_CIR_RLZD);
	}
	
	public List<RelatorioCirurgiaComRetornoVO> listarCirurgiasComRetorno(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) throws BaseException {
		
		this.validarIntervaloDatasPesquisa(dataInicio, dataFim);
		
		List<RelatorioCirurgiaComRetornoVO> cirurgias = getMbcCirurgiasDAO().listarCirurgiasComRetorno(unfSeq, codigoConvenio, seqProcedimento, dataInicio, dataFim);
		for(RelatorioCirurgiaComRetornoVO cirurgia : cirurgias) {
			List<MbcAnestesiaCirurgias> anestesias = getMbcAnestesiaCirurgiasDAO().listarTipoAnestesiasPorCrgSeq(cirurgia.getCrgSeq());
			if(anestesias != null && !anestesias.isEmpty()) {
				cirurgia.setTipoAnestesia(anestesias.get(0).getMbcTipoAnestesias().getDescricao());
			}
			List<MbcProcEspPorCirurgias> procEsp = getMbcProcEspPorCirurgiasDAO().pesquisarProcedimentosNotaAtivosPorCirurgiaProcedimento(cirurgia.getCrgSeq(), seqProcedimento);
			if(procEsp != null && !procEsp.isEmpty()) {
				StringBuffer procedimentos = new StringBuffer(500);
				for(MbcProcEspPorCirurgias procedimento: procEsp) {
					if(procedimentos.length() != 0) {
						procedimentos.append(BR);
					}
					procedimentos.append(procedimento.getProcedimentoCirurgico().getDescricao());
				}
				procedimentos.append(BR + "Téc. Anestésica: " + cirurgia.getTipoAnestesia());
				
				cirurgia.setProcedimentos(procedimentos.toString());
			}
			
			
			List<MbcProfCirurgias> anestesistas = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorFuncao(cirurgia.getCrgSeq(), DominioFuncaoProfissional.ANP);
			if(anestesistas  != null && !anestesistas.isEmpty()) {
				RapPessoasFisicas pessoa = anestesistas.get(0).getServidor().getPessoaFisica();
				if(pessoa != null) {
					cirurgia.setAnestesista(((pessoa.getNomeUsual() != null) ? pessoa.getNomeUsual() : pessoa.getNome()));
				}
			}
		}
		
		return cirurgias;
	}
	
	public String gerarRelatorioCirurgiasComRetornoCSV(Short unfSeq, Short codigoConvenio, Integer seqProcedimento, Date dataInicio, Date dataFim) throws BaseException, IOException{
		
		List<RelatorioCirurgiaComRetornoVO> cirurgias = this.listarCirurgiasComRetorno(unfSeq, codigoConvenio, seqProcedimento, dataInicio, dataFim);
		
		final File file = File.createTempFile(DominioNomeRelatorio.FATR_INT_AIH_POR_SSM.toString(), EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		
		out.write("Data/Início"+SEPARADOR+"Sala"+SEPARADOR+"Agenda"+SEPARADOR+"Regime"+SEPARADOR+
		"Prontuário"+SEPARADOR+"Paciente"+SEPARADOR+"Convênio"+SEPARADOR+"Plano"+SEPARADOR+"Especialidade"+SEPARADOR+
		"Destino"+SEPARADOR+"Procedimento"+SEPARADOR+"Téc. Anestésica"+SEPARADOR+"Méd. Responsável"+SEPARADOR+"Méd. Anestesista"+QUEBRA_LINHA);
		
		Integer comRetorno = 0;
		
		for(RelatorioCirurgiaComRetornoVO cirurgia : cirurgias) {
			out.write(DateUtil.dataToString(cirurgia.getData(),"dd/MM/yyyy")+SEPARADOR_QUEBRA_LINHA+DateUtil.dataToString(cirurgia.getDataInicio(),"HH:mm")+SEPARADOR+
			cirurgia.getSala()+SEPARADOR+cirurgia.getNroAgenda()+SEPARADOR+cirurgia.getOrigem()+SEPARADOR+
			cirurgia.getProntuario()+SEPARADOR+cirurgia.getNomePaciente()+SEPARADOR+cirurgia.getConvenio()+SEPARADOR+cirurgia.getPlano()+SEPARADOR+cirurgia.getEspecialidade()+SEPARADOR+
			cirurgia.getDestino()+SEPARADOR+removerBReTecAnestesica(cirurgia.getProcedimentos())+SEPARADOR+cirurgia.getTipoAnestesia()+SEPARADOR+removerBReAnestesista(cirurgia.getMedicos())+SEPARADOR+
			((cirurgia.getAnestesista()!=null)?cirurgia.getAnestesista():"")+QUEBRA_LINHA);

			comRetorno++;
		}
		
		out.write(QUEBRA_LINHA);
		out.write("Total de Cirurgias com Retorno"+SEPARADOR+comRetorno+QUEBRA_LINHA);
		out.write("Total de Cirurgias sem Retorno"+SEPARADOR+getMbcCirurgiasDAO().quantidadeCirurgiasSemRetorno(unfSeq, codigoConvenio, seqProcedimento, dataInicio, dataFim)+QUEBRA_LINHA);
		
		out.flush();
		out.close();
		
		return file.getAbsolutePath();
	}

	/*public void downloadedCSV(final String fileName, final String name, final String contentType) throws IOException {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		response.setContentType(contentType);
		
		if(name == null){
			response.setHeader("Content-Disposition","attachment;filename=" + fileName);
		} else {
			response.setHeader("Content-Disposition","attachment;filename=" + name);
		}
		
		response.getCharacterEncoding();
		
		final OutputStream out = response.getOutputStream();
		final Scanner scanner = new Scanner(new FileInputStream(fileName), ENCODE);
		
		while (scanner.hasNextLine()) {
			out.write(scanner.nextLine().getBytes(ENCODE));
			out.write(System.getProperty("line.separator").getBytes(ENCODE));
		}
		
		scanner.close();
		out.flush();
		out.close();
		fc.responseComplete();
	}

	public void downloadCSVRelatorioCirurgiasComRetorno(final String fileName, final String name) throws IOException{	
		downloadedCSV(fileName, name, "text/csv");
	}*/

	protected String removerBReAnestesista(String medicos) {
		if(!StringUtils.isEmpty(medicos)) {
			medicos = StringUtils.substringBefore(medicos, BR);
		}
		
		return medicos;
	}
	
	protected String removerBReTecAnestesica(String procedimentos) {
		if(!StringUtils.isEmpty(procedimentos)) {
			procedimentos = StringUtils.substringBefore(procedimentos, "Téc. Anestésica");
			procedimentos = StringUtils.removeEnd(procedimentos, BR);
			procedimentos = StringUtils.replace(procedimentos, BR, NOVA_LINHA);
			procedimentos = "\""+procedimentos+"\"";
		}
		
		return procedimentos;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

}
