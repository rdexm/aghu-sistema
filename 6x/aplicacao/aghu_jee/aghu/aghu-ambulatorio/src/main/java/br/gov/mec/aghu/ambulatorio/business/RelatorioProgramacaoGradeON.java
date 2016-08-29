package br.gov.mec.aghu.ambulatorio.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioProgramacaoGradeHorarioVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioProgramacaoGradeVO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.registrocolaborador.dao.RapQualificacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioProgramacaoGradeON extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2472660161799283957L;
	private static final Log LOG = LogFactory.getLog(RelatorioProgramacaoGradeON.class);	
	private static final String ENCODE = "ISO-8859-1";
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR = ";";
	private static final String BARRA = " / ";
	
	@Inject
	private RapQualificacaoDAO rapQualificacaoDAO;
	
	@Inject
	private AacConsultasJnDAO aacConsultasJnDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * #6810 - Relatório de Programação de Grade. <br/>Método que monta e gera o arquivo CSV do relatório
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @param grade
	 * @param sigla
	 * @param seqEquipe
	 * @param servico
	 * @param conselho
	 * @return Caminho absoluto do arquivo gerado
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	public String obterCSVProgramacaoGrade(Date dataInicio, Date dataFim, Integer grade, String sigla, 
			Integer seqEquipe, Integer servico, VRapServidorConselho conselho) 
			throws IOException, ApplicationBusinessException {
		
		List<RelatorioProgramacaoGradeVO> listaVO = null;
		Integer matricula = null;
		Short vinculo = null;
		
		if (conselho != null && conselho.getId() != null) {
			matricula = conselho.getId().getMatricula();
			vinculo = conselho.getId().getVinCodigo();
		}
		
		List<Object[]> lista = this.aacGradeAgendamenConsultasDAO.obterGradeAgendamentoConsultas(dataInicio, dataFim, grade, sigla, seqEquipe, servico, matricula, vinculo);
		
		if (lista == null || lista.isEmpty()) {
			return null;
		}
		
		final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_PROGRAMACAO_GRADE.toString(), DominioNomeRelatorio.EXTENSAO_CSV);
		
		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		out.write(this.gerarCabecalhoDoRelatorio());
		
		listaVO = construirProgramacaoGrade(lista, dataInicio, dataFim);
		if (listaVO != null && !listaVO.isEmpty()) {
			for (int i = 0; i < listaVO.size(); i++) {
				RelatorioProgramacaoGradeVO vo = listaVO.get(i);
				out.write(this.gerarCorpoDoRelatorio(vo));
			}
		}
		
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}
	
	/**
	 * #6810 - Relatório de Programação de Grade
	 * <br/>Método que gera os nomes do cabeçalho das colunas do arquivo CSV.
	 * 
	 * @return String de uma linha com os nomes das colunas
	 * @throws ApplicationBusinessException 
	 */
	private String gerarCabecalhoDoRelatorio() throws ApplicationBusinessException {
		AghParametros parametroLabelSetor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA);
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("TITLE_CSV_SIGLA_ESPECIALIDADE"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_NOME_ESPECIALIDADE"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_GRADE"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_CODIGO_PAGADOR"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_NOME_PAGADOR"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_CODIGO_EQUIPE"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_NOME_EQUIPE"))
			   .append(SEPARADOR)
			   .append(parametroLabelSetor.getVlrTexto()).append(getResourceBundleValue("TITLE_CSV_BARRA_SALA"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_REGISTRO_CONSELHO_PROFISSIONAL"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_NOME_PROFISSIONAL"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_HORA"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_DIA_PROGRAMADO"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_QUANTIDADE"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_TIPO"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_SALA"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_TPS_TIPO"))
			   .append(SEPARADOR)
			   .append(getResourceBundleValue("TITLE_CSV_DIA_SEMANA"))
			   .append(QUEBRA_LINHA);
		
		return builder.toString().toUpperCase();
	}
	
	/**
	 * #6810 - Relatório de Programação de Grade
	 * <br/>Método que gerencia a criação das linhas que compõe o corpo do arquivo CSV.
	 * 
	 * @param vo - {@link RelatorioProgramacaoGradeVO} 
	 * @return String com as informações do corpo do CSV
	 */
	private String gerarCorpoDoRelatorio(RelatorioProgramacaoGradeVO vo) {
		String registroConselhoProfissional = "";
		String nomeProfissional = "";
		List<RapQualificacao> listaQualificacao = this.rapQualificacaoDAO.pesquisarConselhoProfissionalPorServidor(vo.getPerSerMatricula(), vo.getPerSerVinculo());
		if (listaQualificacao != null && !listaQualificacao.isEmpty()) {
			RapQualificacao qualificacao = listaQualificacao.get(0);
			if (qualificacao.getNroRegConselho() != null) {
				registroConselhoProfissional = qualificacao.getNroRegConselho();
			}
			if (qualificacao.getPessoaFisica() != null && qualificacao.getPessoaFisica().getNome() != null) {
				nomeProfissional = qualificacao.getPessoaFisica().getNome();
			}
		}
		StringBuilder builder = new StringBuilder();
		if (vo.getHorarios() != null && !vo.getHorarios().isEmpty()) {
			List<String> dias = new ArrayList<String>();
			List<String> horarios = new ArrayList<String>();
			this.carregarListaDiaHorario(vo.getHorarios(), dias, horarios);
			for (int i = 0; i < horarios.size(); i++) {
				String hora = horarios.get(i);
				for (int j = 0; j < dias.size(); j++) {
					String dia = dias.get(j);
					RelatorioProgramacaoGradeHorarioVO horarioVO = this.obterGradeHorarioPorHoraDia(horarios.get(i), dias.get(j), vo.getHorarios());
					if (horarioVO != null) {
						builder.append(validarCampoNulo(vo.getSiglaEspecialidade())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getNomeEspecialidade())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getGrdSeq())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getSeqPagador())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getDescricaoPagador())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getSeqEquipe())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getNomeEquipe())).append(SEPARADOR)
						.append(validarCampoNulo(concatenarCampos(vo.getSiglaUfSala(), vo.getSala().toString(), "\\"))).append(SEPARADOR)
						.append(validarCampoNulo(registroConselhoProfissional)).append(SEPARADOR)
						.append(validarCampoNulo(nomeProfissional)).append(SEPARADOR)
						.append(validarCampoNulo(horarioVO.getHora())).append(SEPARADOR)
						.append(validarCampoNulo(horarioVO.getDiaSemana())).append(SEPARADOR)
						.append(validarCampoNulo(horarioVO.getQuantidade())).append(SEPARADOR)
						.append(validarCampoNulo(horarioVO.getCaaSigla())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getSala())).append(SEPARADOR)
						.append(validarCampoNulo(horarioVO.getCaaSigla())).append(SEPARADOR)
						.append(validarCampoNulo(horarioVO.getDiaSemana())).append(QUEBRA_LINHA);
					} else {
						builder.append(validarCampoNulo(vo.getSiglaEspecialidade())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getNomeEspecialidade())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getGrdSeq())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getSeqPagador())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getDescricaoPagador())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getSeqEquipe())).append(SEPARADOR)
						.append(validarCampoNulo(vo.getNomeEquipe())).append(SEPARADOR)
						.append(validarCampoNulo(concatenarCampos(vo.getSiglaUfSala(), vo.getSala().toString(), "\\"))).append(SEPARADOR)
						.append(validarCampoNulo(registroConselhoProfissional)).append(SEPARADOR)
						.append(validarCampoNulo(nomeProfissional)).append(SEPARADOR)
						.append(validarCampoNulo(hora)).append(SEPARADOR)
						.append(validarCampoNulo(null)).append(SEPARADOR)
						.append(validarCampoNulo(null)).append(SEPARADOR)
						.append(validarCampoNulo(null)).append(SEPARADOR)
						.append(validarCampoNulo(null)).append(SEPARADOR)
						.append(validarCampoNulo(null)).append(SEPARADOR)
						.append(validarCampoNulo(DominioDiaSemana.getDiaDaSemana(Integer.valueOf(dia)).toString())).append(QUEBRA_LINHA);
					}
				}
			}
		}
		return builder.toString().toUpperCase();
	}
	
	private RelatorioProgramacaoGradeHorarioVO obterGradeHorarioPorHoraDia(String hora, String dia, List<RelatorioProgramacaoGradeHorarioVO> horariosVO) {
		for (RelatorioProgramacaoGradeHorarioVO horarioVO : horariosVO) {
			if (horarioVO.getHora().equals(hora) && horarioVO.getDia().equals(dia)) {
				return horarioVO;
			}
		}
		return null;
	}

	private void carregarListaDiaHorario(List<RelatorioProgramacaoGradeHorarioVO> horariosVO, List<String> dias, List<String> horarios) {
		for (RelatorioProgramacaoGradeHorarioVO horarioVO : horariosVO) {
			if (!dias.contains(horarioVO.getDia())) {
				dias.add(horarioVO.getDia());
			}
			if (!horarios.contains(horarioVO.getHora())) {
				horarios.add(horarioVO.getHora());
			}
		}
		Collections.sort(dias);
		Collections.sort(horarios);
	}
	
	private String validarCampoNulo(Object object) {
		if (object != null) {
			return object.toString();
		}
		return "";
	}
	
	/**
	 * Obter o registro profissional concatenado com o nome do profissional.
	 * ON2 da estória #6810
	 * @param matricula
	 * @param vinculo
	 * @return String
	 */
	public String obterProfissional(Integer matricula, Short vinculo){
		
		if(matricula != null && vinculo != null){
			List<RapQualificacao> qualificacao = this.rapQualificacaoDAO.pesquisarConselhoProfissionalPorServidor(matricula, vinculo);
			if(qualificacao != null && !qualificacao.isEmpty()){
				for (RapQualificacao rapQualificacao : qualificacao) {
					if(rapQualificacao.getNroRegConselho() != null){
						return rapQualificacao.getNroRegConselho() + " " + rapQualificacao.getPessoaFisica().getNome();
					}else{
						return rapQualificacao.getPessoaFisica().getNome();
					}
				}
			}
		}else{
			return " ";
		}
		return null;
	}
	
	/**
	 * @ORADB AACC_BUSCA_CAA_GERADA
	 * Obter a condição de atendimento gerada.
	 * F1 da estória #6810
	 * @param numero
	 * @param condicaoAtendimento
	 * @return Short
	 */
	public Short buscarCondicaoAtendimentoGerada(Integer numero, Short fagCaaSeq){
		Short retorno = null;
		AacConsultasJn consultaJn = this.aacConsultasJnDAO.pesquisarCondicaoAtendimentoGerada(numero);
		
		if(consultaJn == null){
			retorno = fagCaaSeq;
		}else{
			if(consultaJn.getFagPgdSeq() == null || consultaJn.getFagCaaSeq() == null || consultaJn.getFagTagSeq() == null){
				retorno = fagCaaSeq;
			}else{
				retorno = consultaJn.getFagCaaSeq();
			}
		}
		return retorno;
	}
	/**
	 * Estória #6810
	 * Método que realiza a consulta C2 para preenchimentos dos campos do relatório.
	 */
	public List<RelatorioProgramacaoGradeVO> obterRelatorioProgramacaoGrade(Date dataInicio, Date dataFim, 
			Integer grade, String sigla, Integer seqEquipe, Integer servico, VRapServidorConselho conselho){
		List<RelatorioProgramacaoGradeVO> listaVOConstruida = null;
		List<RelatorioProgramacaoGradeVO> listaVOAux = new ArrayList<RelatorioProgramacaoGradeVO>();
		Integer matricula = null;
		Short vinculo = null;
		if(conselho != null && conselho.getId() != null){
			matricula = conselho.getId().getMatricula();
			vinculo = conselho.getId().getVinCodigo();
		}
		List<Object[]> lista = this.aacGradeAgendamenConsultasDAO.obterGradeAgendamentoConsultas(dataInicio, 
				dataFim, grade, sigla, seqEquipe, servico, matricula, vinculo);
		if(lista != null && !lista.isEmpty()){
			listaVOConstruida = construirProgramacaoGrade(lista, dataInicio, dataFim);
			if(listaVOConstruida != null && !listaVOConstruida.isEmpty()){
				for (RelatorioProgramacaoGradeVO retorno : listaVOConstruida) {
					if(retorno.getSiglaEspecialidade() != null && retorno.getNomeEspecialidade() != null){					
						retorno.setEspecialidade(concatenarCampos(retorno.getSiglaEspecialidade(), retorno.getNomeEspecialidade(), BARRA));
					}
					if(retorno.getSeqPagador() != null && retorno.getDescricaoPagador() != null){					
						retorno.setPagador(concatenarCampos(retorno.getSeqPagador().toString(), retorno.getDescricaoPagador(), " "));
					}
					if(retorno.getSiglaUfSala() != null && retorno.getSala() != null){					
						retorno.setSetorSala(concatenarCampos(retorno.getSiglaUfSala(), retorno.getSala().toString(), BARRA));
					}
					if(retorno.getSeqEquipe() != null && retorno.getNomeEquipe() != null){					
						retorno.setEquipe(concatenarCampos(retorno.getSeqEquipe().toString(), retorno.getNomeEquipe(), " "));
					}
					if(retorno.getPerSerMatricula() != null && retorno.getPerSerVinculo() != null){					
						retorno.setProfissional(obterProfissional(retorno.getPerSerMatricula(), retorno.getPerSerVinculo()));
					}
					listaVOAux.add(retorno);
				}
			}
		}
		return listaVOAux;
	}
	
	/**
	 * Retorna as duas strings concatenadas com o separador selecionado caso a segunda string seja diferente de nulo. 
	 */
	private String concatenarCampos(String dsc1, String dsc2, String separador) {
		if (dsc1 != null) {
			StringBuffer sb = new StringBuffer(StringUtils.EMPTY);
			sb.append(dsc1);
			if (dsc2 != null && !StringUtils.isBlank(dsc2)) {
				return sb.append(separador).append(dsc2).toString();
			}
			return sb.toString();
		}
		return null;
	}

	/**
	 * Atribui valores para as variaveis do relatorio com os valores retornados de C2.
	 */
	private List<RelatorioProgramacaoGradeVO> construirProgramacaoGrade(List<Object[]> lista, Date dataInicio, Date dataFim) {
		List<RelatorioProgramacaoGradeVO> listaVO = new ArrayList<RelatorioProgramacaoGradeVO>();
		for (Object[] object : lista) {			
			RelatorioProgramacaoGradeVO vo = new RelatorioProgramacaoGradeVO();
			vo.setSiglaEspecialidade((String) object[0]);
			vo.setNomeEspecialidade((String) object[1]);
			vo.setGrdSeq((Integer) object[2]);
//			vo.setTpsTipo((String) object[3]);
			vo.setSeqPagador((Short) object[3]);
			vo.setDescricaoPagador((String) object[4]);
			vo.setSala((Byte) object[5]);
			vo.setSiglaUfSala((String) object[6]);
			vo.setSeqEquipe((Integer) object[7]);
			vo.setNomeEquipe((String) object[8]);
			vo.setPerSerMatricula((Integer) object[9]);
			vo.setPerSerVinculo((Short) object[10]);

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dataInicioFormat = format.format(dataInicio);
			String dataFimFormat = format.format(dataFim);
			vo.setHorarios(construirHorariosConsultas(this.aacConsultasDAO.obterHorarioConsultas(dataInicioFormat, dataFimFormat, vo.getGrdSeq()), vo));
			
			listaVO.add(vo);
		}
		
		return listaVO;
	}
	
	/**
	 * Atribui valores para as variaveis do relatorio com os valores retornados de C2.
	 */
	private List<RelatorioProgramacaoGradeHorarioVO> construirHorariosConsultas(List<Object[]> listaConsultas, RelatorioProgramacaoGradeVO vo){
		List<RelatorioProgramacaoGradeHorarioVO> listaHorarios = new ArrayList<RelatorioProgramacaoGradeHorarioVO>();
		RelatorioProgramacaoGradeVO linha = new RelatorioProgramacaoGradeVO();
		
		if(listaConsultas != null && !listaConsultas.isEmpty()){
			for (Object[] object : listaConsultas) {
				RelatorioProgramacaoGradeHorarioVO horarioVO = new RelatorioProgramacaoGradeHorarioVO();
				horarioVO.setDiaSemana((String) object[0]);
				horarioVO.setDia((String) object[1]);
				horarioVO.setHora((String) object[2]);
				horarioVO.setCaaSigla((String) object[3]);
				horarioVO.setQuantidade((BigDecimal) object[4]);
				horarioVO.setTipo(concatenarCampos(horarioVO.getCaaSigla(), horarioVO.getQuantidade().toString(), " "));
				if(!linha.getListaHora().contains(horarioVO.getHora())){
					linha.getListaHora().add(horarioVO.getHora());
				}
				vo.setListaHora(linha.getListaHora());
				listaHorarios.add(horarioVO);
			}
		}
		return listaHorarios;
	}

}
