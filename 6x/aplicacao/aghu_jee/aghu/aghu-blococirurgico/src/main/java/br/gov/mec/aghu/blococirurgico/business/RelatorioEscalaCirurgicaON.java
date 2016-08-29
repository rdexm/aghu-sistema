package br.gov.mec.aghu.blococirurgico.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaDiagnosticoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoUtilCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMatOrteseProtCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirg;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.paciente.vo.SubRelatorioEscalaCirurgiasOrteseProteseVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class RelatorioEscalaCirurgicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioEscalaCirurgicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcMatOrteseProtCirgDAO mbcMatOrteseProtCirgDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;

	@Inject
	private MbcEquipamentoUtilCirgDAO mbcEquipamentoUtilCirgDAO;

	@Inject
	private MbcAgendaDiagnosticoDAO mbcAgendaDiagnosticoDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	private static final long serialVersionUID = -5744342433996874121L;

	public static final String HORA_HHMM = "HH:mm";
	public static final String YYYY = "yyyy";
	
	public enum RelatorioEscalaCirurgicaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RELATORIO_ESCALA_VAZIO
	}

	/**
	 * Relatório de escala cirurgica (ESCALA DEFINITIVA)
	 * 
	 * @param unidadesFuncional
	 * @param dataCirurgia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<EscalaCirurgiasVO> pesquisarEscalaCirurgica(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia , Integer grupoMat) throws ApplicationBusinessException {

		List<EscalaCirurgiasVO> resultado = new ArrayList<EscalaCirurgiasVO>();

		// C1: Consulta principal
		List<MbcCirurgias> lista = this.getMbcCirurgiasDAO().pesquisarEscalaCirurgica(unidadesFuncional.getSeq(), dataCirurgia);
		
		if (lista.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioEscalaCirurgicaONExceptionCode.MENSAGEM_RELATORIO_ESCALA_VAZIO);
		}

		for (MbcCirurgias cirurgia : lista) {

			/*
			 * Seta atributos através da CONSULTA PRINCIPAL
			 */

			EscalaCirurgiasVO vo = new EscalaCirurgiasVO();

			vo.setSeq(cirurgia.getSeq().toString());

			MbcSalaCirurgica salaCirurgica = cirurgia.getSalaCirurgica();
			if (salaCirurgica != null) {
				String sciSeqp = StringUtil.adicionaZerosAEsquerda(salaCirurgica.getId().getSeqp(), 2);
				vo.setSciSeqp(sciSeqp);
			} else {
				vo.setSciSeqp("");
			}

			
			vo.setDthrInicioCirg(this.formataDatas(cirurgia.getDataInicioCirurgia(), HORA_HHMM));
			vo.setDthrFimCirg(this.formataDatas(cirurgia.getDataFimCirurgia(), HORA_HHMM));
			vo.setContaminacao(this.converterBooleanoSimNao(cirurgia.getContaminacao()));
			vo.setDigitaNotaSala(this.converterBooleanoSimNao(cirurgia.getDigitaNotaSala()));
			vo.setNumeroAgenda(String.valueOf(cirurgia.getNumeroAgenda()));

			AipPacientes paciente = cirurgia.getPaciente();

			// Origem do paciente: Chamada para PROCEDURE MBCC_LOCAL_AIP_PAC(CRG.PAC_CODIGO) ORIGEM_PAC
			vo.setOrigemPacCirg(getEscalaCirurgiasON().pesquisaQuarto(paciente.getCodigo()));

			vo.setIndPrecaucaoEspecial(this.converterBooleanoSimNao(cirurgia.getPrecaucaoEspecial()));
			vo.setUnfSeq(String.valueOf(cirurgia.getUnidadeFuncional().getSeq()));

			String prontuarioFormatado = CoreUtil.formataProntuario(paciente.getProntuario());
			vo.setProntuario(prontuarioFormatado);

			// Nome
			vo.setNome(StringUtils.substring(paciente.getNome(), 0, 40));

			// Idade
			vo.setIdade(this.calcularIdadePaciente(paciente.getDtNascimento()));

			// Convênio
			vo.setConvenio(cirurgia.getConvenioSaude().getDescricao());

			MbcAgendas agenda = cirurgia.getAgenda();

			if (agenda != null) {

				// Lado da cirurgia
				vo.setLadoCirurgia(this.obterLadoCirurgia(agenda.getLadoCirurgia()));

				// Diagnóstico
				MbcAgendaDiagnostico agendaDiagnostico = getMbcAgendaDiagnosticoDAO().obterAgendaDiagnosticoEscalaCirurgicaPorAgenda(agenda.getSeq());

				if (agendaDiagnostico != null) {
					vo.setDiagnostico(this.obterDiagnostico(agendaDiagnostico.getAghCid()));
				}
				
				
			}

			/*
			 * Seta atributos através das CONSULTAS SECUNDÁRIAS
			 */

			// C2: Nome do cirurgião
			this.obterNomeCirurgiao(vo, cirurgia, unidadesFuncional);

			// C3: Órtese e prótese e quantidade solicitada
			this.obterOrteseProtese(vo, cirurgia);
//			if(agenda.getRequisicoesOpmes() != null){
//				SubRelatorioEscalaCirurgiasOrteseProteseVO subVO = new SubRelatorioEscalaCirurgiasOrteseProteseVO();
//				subVO.setOrteseProt(agenda.getRequisicoesOpmes().get(0).getObservacaoOpme());
//				vo.getSubRelatorioEscalaCirurgiasOrteseProtese().add(subVO);
//			}
			// SETA OBSERVACOES DE OPME
			vo.setObservacaoTitle(null);
			if (agenda != null) {
				if(agenda.getRequisicoesOpmes() != null){
					if(agenda.getRequisicoesOpmes().size() > 0){
						if(agenda.getRequisicoesOpmes().get(0).getObservacaoOpme() != null){
							vo.setObservacao(agenda.getRequisicoesOpmes().get(0).getObservacaoOpme());
							vo.setObservacaoTitle("Observações OPMEs:");
						}
					}
				}
			}
			

			// C4: Procedimento cirúrgico
			this.obterProcedimentoCirurgico(vo, cirurgia);

			// C5: Componente sanguíneo
			this.obterComponenteSanguineo(vo, cirurgia, "Sangue: ");

			// C6: Descrição do Exame
			this.obterDescricaoExame(vo, cirurgia);

			// C7: Anestesista Professor
			this.obterAnestesistaProfessor(vo, cirurgia, unidadesFuncional);

			// C8: Equipamento
			this.obterEquipamento(vo, cirurgia);

			// C9: Material
			this.obterMaterial(vo, cirurgia);
			
			// 
			
			//obter ortese protese OPME
			this.obterMaterialOrteseProteseOPME(vo, agenda, grupoMat);
			

			// Este campo é utilizado na ordenação da consulta
			vo.setDthrInicioOrdem(cirurgia.getDataInicioOrdem());

			resultado.add(vo);
		}

		// Ordena resultado
		this.ordernarPesquisarEscalaCirurgica(resultado);
		
		// adicionar linhas
		resultado.get(0).setEscalaCirurgiasVO(resultado);
		List<EscalaCirurgiasVO> listRetorno = new ArrayList<EscalaCirurgiasVO>();
		listRetorno.add(resultado.get(0));

		return listRetorno;

	}

	/**
	 * Ordena resultado final através da sala, data inicial ou data do ínicio da ordem da cirúrgia
	 * 
	 * @param resultado
	 */
	private void ordernarPesquisarEscalaCirurgica(List<EscalaCirurgiasVO> resultado) {
		if (resultado != null && !resultado.isEmpty()) {
			Collections.sort(resultado, new Comparator<EscalaCirurgiasVO>() {
				@Override
				public int compare(EscalaCirurgiasVO vo1, EscalaCirurgiasVO vo2) {
					return vo1.compareTo(vo2);
				}
			});
		}
	}

	/**
	 * Relatório de escala cirurgica simples
	 * 
	 * @param unidadesFuncional
	 * @param dataCirurgia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<EscalaCirurgiasVO> pesquisarEscalaSimples(AghUnidadesFuncionais unidadesFuncional, Date dataCirurgia) throws ApplicationBusinessException {

		List<EscalaCirurgiasVO> resultado = new ArrayList<EscalaCirurgiasVO>();

		EscalaCirurgiasVO vo = null;
		List<MbcCirurgias> lista = this.getMbcCirurgiasDAO().pesquisarEscalaSimples(unidadesFuncional.getSeq(), dataCirurgia);
		
		if (lista.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioEscalaCirurgicaONExceptionCode.MENSAGEM_RELATORIO_ESCALA_VAZIO);
		}

		Date dataAtual = new Date();
		Integer totalCirurgias = 0;
		
		for (MbcCirurgias cirurgia : lista) {

			vo = new EscalaCirurgiasVO();
			vo.setSeq(cirurgia.getSeq().toString());
			
			MbcSalaCirurgica salaCirurgica = cirurgia.getSalaCirurgica();
			if (salaCirurgica != null) {
				String sciSeqp = StringUtil.adicionaZerosAEsquerda(salaCirurgica.getId().getSeqp(), 2);
				vo.setSciSeqp(sciSeqp);
			} else {
				vo.setSciSeqp("");
			}
			
			vo.setDthrInicioCirg(this.formataDatas(cirurgia.getDataInicioCirurgia(), HORA_HHMM));
			vo.setCspCnvCodigo(cirurgia.getConvenioSaude().getCodigo().toString());
			vo.setNome(cirurgia.getPaciente().getNome());
			int anoAtual = Integer.valueOf(this.formataDatas(dataAtual, YYYY));
			int anoNascimento = Integer.valueOf(this.formataDatas(cirurgia.getPaciente().getDtNascimento(), YYYY));
			vo.setDtNascimento(String.valueOf(anoAtual - anoNascimento));
			vo.setOrigemPacCirg(cirurgia.getOrigemPacienteCirurgia().getDescricao().substring(0, 1));
			if (cirurgia.getPaciente().getProntuario() != null) {
				String prontAux = cirurgia.getPaciente().getProntuario().toString();
				vo.setProntuario1(prontAux);
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (cirurgia.getPaciente().getCodigo() != null) {
				vo.setPacCodigo(cirurgia.getPaciente().getCodigo().toString());
				// Pesquisa por quarto/leito
				vo.setQuarto(getEscalaCirurgiasON().pesquisaQuarto(cirurgia.getPaciente().getCodigo()));
			}

			vo.setIndPrecaucaoEspecial(this.converterBooleanoSimNao(cirurgia.getPrecaucaoEspecial() ? cirurgia.getPrecaucaoEspecial() : null));

			vo.setNomeCirurgiao(getEscalaCirurgiasON().obterCirurgiao(cirurgia.getSeq()));
			vo.setTipoAnestesia(getEscalaCirurgiasON().pesquisaTipoAnestesia(cirurgia.getSeq()));

			totalCirurgias = this.obterProcedimentos(vo, totalCirurgias, cirurgia);

			resultado.add(vo);
		}
		
		if (vo != null) {
			vo.setTotalCirurgias(totalCirurgias.toString());
		}

		return resultado;
	}

	/**
	 * Obtem os procedimentos
	 * @param vo
	 * @param totalCirurgias
	 * @param cirurgia
	 * @return
	 */
	public Integer obterProcedimentos(EscalaCirurgiasVO vo, Integer totalCirurgias, MbcCirurgias cirurgia) {
		// Lista Procedimento Cirurgico
		List<MbcProcEspPorCirurgias> listaProcedimento = getEscalaCirurgiasON().pesquisaProcedimentoCompleto(cirurgia.getSeq());
		StringBuffer procedimentoCirurgico = null;
		String quebra = "\n";

		for (MbcProcEspPorCirurgias procedimento : listaProcedimento) {
			String proc = procedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getDescricao();
			MbcAgendas agenda = procedimento.getCirurgia().getAgenda();
			DominioLadoCirurgiaAgendas ladoCirurgia =  null;
			String lado = null;
			if(agenda!=null && agenda.getLadoCirurgia()!=null){
				ladoCirurgia = agenda.getLadoCirurgia();
				lado = this.obterLadoCirurgia(ladoCirurgia);
			}
			totalCirurgias = totalCirurgias + 1;
			if (procedimentoCirurgico != null) {
				procedimentoCirurgico.append("                                 ").append(proc);
				if(lado!=null){
					procedimentoCirurgico.append(quebra);
					procedimentoCirurgico.append(quebra);
					procedimentoCirurgico.append(lado);	
				}
			} else {
				procedimentoCirurgico = new StringBuffer(proc);
				if(ladoCirurgia!=null){
					procedimentoCirurgico.append(quebra);
					procedimentoCirurgico.append(quebra);
					procedimentoCirurgico.append(lado);	
				}
			}
		}
		if (procedimentoCirurgico != null) {
			vo.setProcedimentoCirurgico(procedimentoCirurgico.toString().replace("null", ""));
		}
		return totalCirurgias;
	}

	/**
	 * ORADBF FUNCTION CF_TITULO_ESCFORMULA
	 * 
	 * @param tipoEscala
	 * @param dataCirurgia
	 * @return
	 */
	public String pesquisarTituloEscala(DominioTipoEscala tipoEscala, Date dataCirurgia) {

		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		StringBuffer titulo = new StringBuffer(128);

		if (tipoEscala!=null &&  tipoEscala.equals(DominioTipoEscala.D)) { // Definitiva
			titulo.append("ESCALA DEFINITIVA PARA ").append(data.format(dataCirurgia));
		} else if (tipoEscala!=null && tipoEscala.equals(DominioTipoEscala.P)) { // Prévia
			titulo.append("ESCALA PRÉVIA PARA ").append(data.format(dataCirurgia)).append(" (PASSÍVEL DE ALTERAÇÕES)");
		} else { // Agenda prevista
			titulo.append("AGENDA PREVISTA - ").append(data.format(dataCirurgia)).append(" (PASSÍVEL DE ALTERAÇÕES)");
		}

		return titulo.toString();
	}

	/**
	 * Obtém o nome do cirurgião através da cirurgia
	 * 
	 * @param cirurgia
	 * @param unidadesFuncional
	 * @return
	 */
	public void obterNomeCirurgiao(EscalaCirurgiasVO vo, MbcCirurgias cirurgia, AghUnidadesFuncionais unidadesFuncional) {

		MbcProfCirurgias profCirurgia = this.getMbcProfCirurgiasDAO().obterCirurgiaoEscalaCirurgicaPorCirurgia(cirurgia.getSeq(), unidadesFuncional.getSeq());

		String nomeCirurgiao = "";

		if (profCirurgia != null) {

			RapPessoasFisicas pessoaFisica = profCirurgia.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica();

			if (pessoaFisica.getNomeUsual() == null) {
				nomeCirurgiao = StringUtils.substring(pessoaFisica.getNome(), 0, 15);
			} else {
				nomeCirurgiao = pessoaFisica.getNomeUsual();
			}

		}

		if (StringUtils.isNotEmpty(nomeCirurgiao)) {
			nomeCirurgiao = "Cir: ".concat(nomeCirurgiao.toUpperCase());
		}

		vo.setNomeCirurgiao(nomeCirurgiao); // Seta NOME DO CIRURGIÃO
	}

	/**
	 * Obtém material de órtese e prótese
	 * 
	 * @param vo
	 * @param cirurgia
	 */
	public void obterOrteseProtese(EscalaCirurgiasVO vo, MbcCirurgias cirurgia) {

		List<MbcMatOrteseProtCirg> listaMatOrteseProtCirg = this.getMbcMatOrteseProtCirgDAO().pesquisarOrteseProteseEscalaCirurgicaPorCirurgia(cirurgia.getSeq());

		List<SubRelatorioEscalaCirurgiasOrteseProteseVO> listaFinal = new ArrayList<SubRelatorioEscalaCirurgiasOrteseProteseVO>();
		int rowNum = 0;
		for (MbcMatOrteseProtCirg item : listaMatOrteseProtCirg) {
			rowNum = rowNum+ 1;
			SubRelatorioEscalaCirurgiasOrteseProteseVO subVO = new SubRelatorioEscalaCirurgiasOrteseProteseVO();
			if (rowNum == 1) {
				subVO.setOrteseProt("OPMEs: ".concat(item.getScoMaterial().getNome()));
				subVO.setQtdSolic(item.getQtdSolic().toString());
			} else {
				subVO.setOrteseProt(item.getScoMaterial().getNome());
				subVO.setQtdSolic(item.getQtdSolic().toString());
			}
			listaFinal.add(subVO);
		}
		vo.setSubRelatorioEscalaCirurgiasOrteseProtese(listaFinal); // Seta MATERIAL e QUANTIDADE SOLICITADA

	}

	/**
	 * Obtém o procedimento cirúrgico
	 * 
	 * @param vo
	 * @param cirurgia
	 */
	public void obterProcedimentoCirurgico(EscalaCirurgiasVO vo, MbcCirurgias cirurgia) {

		// Obtém o o valor da nota agenda
		DominioIndRespProc valorNotaAgenda = this.obterNotaAgenda(cirurgia);

		List<MbcProcEspPorCirurgias> listaprocEspPorCirurgia = this.getMbcProcEspPorCirurgiasDAO().getPesquisarProcedimentoCirurgicoEscalaCirurgica(cirurgia.getSeq(),
				valorNotaAgenda);
		
		StringBuffer listaProcedimento = new StringBuffer();
		String quebra = "\n";
		for (MbcProcEspPorCirurgias item : listaprocEspPorCirurgia) {
			
			if (listaprocEspPorCirurgia.indexOf(item) > 0) {
				listaProcedimento.append(quebra);
			}
			MbcProcedimentoCirurgicos procedimentoCirurgico = item.getProcedimentoCirurgico();

			listaProcedimento.append(procedimentoCirurgico.getSeq().toString().concat(" - ")
					.concat(procedimentoCirurgico.getDescricao()));
		}
		vo.setDescricaoPci(listaProcedimento.toString()); // Seta DESCRIÇÃO DO PROCEDIMENTO
	}

	/**
	 * Obtém o Material ortese protese (OPME)
	 * 
	 * @param vo
	 * @param cirurgia
	 * @param grupoMat
	 * 
	 */
	public void obterMaterialOrteseProteseOPME(EscalaCirurgiasVO vo, MbcAgendas agendas, Integer grupoMat) {
		vo.setSubRelatorioOPMEOrteseProtese(getMbcAgendasDAO().listarMateriaisOrteseProteseOPME(agendas.getSeq(),grupoMat));
	}

	
	/**
	 * 
	 * ORADB FUNCTION CF_NOTA_AGDAFORMULA Obtém o o valor da nota agenda
	 * 
	 * @param cirurgia
	 * @return
	 */
	public DominioIndRespProc obterNotaAgenda(MbcCirurgias cirurgia) {
		List<DominioIndRespProc> listavaloresNotaAgenda = this.getMbcProcEspPorCirurgiasDAO().pesquisarStatusResponsabilidadeProcedimentosCirurgicos(cirurgia.getSeq(), ".");
		return listavaloresNotaAgenda.isEmpty() ? DominioIndRespProc.AGND : listavaloresNotaAgenda.get(0);
	}

	/**
	 * Obtém o equipamento
	 * 
	 * @param vo
	 * @param cirurgia
	 */
	public void obterEquipamento(EscalaCirurgiasVO vo, MbcCirurgias cirurgia) {

		List<MbcEquipamentoUtilCirg> listaEquipamentoUtilCirg = this.getMbcEquipamentoUtilCirgDAO().pesquisarMbcEquipamentoUtilCirgPorCirurgia(cirurgia.getSeq(), null);

		StringBuffer listaEquipamento = new StringBuffer();
		String titulo = "Equipamento: ";
		for (MbcEquipamentoUtilCirg item : listaEquipamentoUtilCirg) {
			if(listaEquipamento.length() > 0){
				titulo = "             ";
			}
			this.popularListaValores(listaEquipamento, titulo.concat(item.getMbcEquipamentoCirurgico().getDescricao()));
		}

		vo.setEquipamento(listaEquipamento.toString()); // Seta EQUIPAMENTO

	}

	/**
	 * Obtém o componente sanguíneo
	 * 
	 * @param vo
	 * @param cirurgia
	 * 	 * @param cirurgia
	 */
	public void obterComponenteSanguineo(EscalaCirurgiasVO vo, MbcCirurgias cirurgia, String titulo) {
		
		//String auxiliar para setar o espaçamento do relatório antigo
		String tituloSangue = "Sangue: ";
		if(StringUtils.isEmpty(titulo)){
			titulo = "";
		}

		List<MbcSolicHemoCirgAgendada> listaSolicHemoCirgAgendada = this.getMbcSolicHemoCirgAgendadaDAO().pesquisarComponenteSanguineosEscalaCirurgica(cirurgia.getSeq());

		StringBuffer listaSangue = new StringBuffer(20);
		String quebra = "\n";
		for (MbcSolicHemoCirgAgendada item : listaSolicHemoCirgAgendada) {

			AbsComponenteSanguineo componenteSanguineo = item.getAbsComponenteSanguineo();

			if (listaSangue.length() == 0 && StringUtils.isNotEmpty(titulo)) {
				listaSangue.append(titulo);
			}
			if (listaSolicHemoCirgAgendada.indexOf(item) > 0) {
				listaSangue.append(quebra);
				if (tituloSangue.equals(titulo)) {
					listaSangue.append("        ");
				} else {
					listaSangue.append("                   ");
				}
			}
			listaSangue.append(componenteSanguineo.getDescricao().concat("  "));
			
			// Considera a quantidade ML quando a quantidade estiver vazia
			Object quantidade = CoreUtil.nvl(item.getQuantidade(), item.getQtdeMl() + "ML");
			listaSangue.append(quantidade.toString());
		}
		vo.setSangue(listaSangue.toString()); // Seta SANGUE
	}

	/**
	 * Obtém a descrição do exame
	 * 
	 * @param cirurgia
	 */
	public void obterDescricaoExame(EscalaCirurgiasVO vo, MbcCirurgias cirurgia) {

		List<MbcSolicitacaoEspExecCirg> listaSolicitacaoEspExecCirg = this.getMbcSolicitacaoEspExecCirgDAO().pesquisarExameSolicitacaoEspecialidadeExecutaEscalaCirurgica(
				cirurgia.getSeq());

		StringBuffer listaDescricaoExame = new StringBuffer();
		String titulo = "Exames: ";
		
		for (MbcSolicitacaoEspExecCirg item : listaSolicitacaoEspExecCirg) {
			if(listaDescricaoExame.length() > 0){
				titulo = "        ";
			}
			MbcNecessidadeCirurgica necessidadeCirurgica = item.getMbcNecessidadeCirurgica();

			this.popularListaValores(listaDescricaoExame, titulo.concat(necessidadeCirurgica.getDescricao()));
			
		}
		vo.setExame(listaDescricaoExame.toString()); // Seta EXAME/DESCRIÇÃO DO EXAME

	}

	/**
	 * Obtém o nome do anestesista professor
	 * 
	 * @param cirurgia
	 * @param unidadesFuncional
	 * @return
	 */
	public void obterAnestesistaProfessor(EscalaCirurgiasVO vo, MbcCirurgias cirurgia, AghUnidadesFuncionais unidadesFuncional) {

		MbcProfCirurgias profCirurgia = this.getMbcProfCirurgiasDAO().obterAnestesistaProfessorEscalaCirurgicaPorCrgSeq(cirurgia.getSeq(), unidadesFuncional.getSeq());

		String nomeAnp = "";

		if (profCirurgia != null) {

			RapPessoasFisicas pessoaFisica = profCirurgia.getServidor().getPessoaFisica();

			if (pessoaFisica.getNomeUsual() == null) {
				nomeAnp = StringUtils.substring(pessoaFisica.getNome(), 0, 15);
			} else {
				nomeAnp = pessoaFisica.getNomeUsual();
			}

		}

		if (StringUtils.isNotEmpty(nomeAnp)) {
			vo.setNomeAnp("Anestesista Professor: ".concat(nomeAnp.toUpperCase())); // Seta ANESTESISTA PROFESSOR
		}

	}

	/**
	 * Obtém o material
	 * 
	 * @param cirurgia
	 */
	public void obterMaterial(EscalaCirurgiasVO vo, MbcCirurgias cirurgia) {

		List<MbcSolicitacaoEspExecCirg> listaSolicitacaoEspExecCirg = this.getMbcSolicitacaoEspExecCirgDAO().
		pesquisarMaterialSolicitacaoEspecialidadeExecutaEscalaCirurgica(cirurgia.getSeq(),null);

		StringBuffer listaMaterial = new StringBuffer();
		String titulo = "Sol. Especial: ";
		
		for (MbcSolicitacaoEspExecCirg item : listaSolicitacaoEspExecCirg) {

			MbcNecessidadeCirurgica necessidadeCirurgica = item.getMbcNecessidadeCirurgica();
			
			if(listaMaterial.length() > 0){
				titulo = "               ";
			}
			
			String descricao = null;

			if (Boolean.TRUE.equals(necessidadeCirurgica.getIndExigeDescSolic())) {
				descricao = item.getDescricao();
			} else {
				descricao = necessidadeCirurgica.getDescricao();
			}

			this.popularListaValores(listaMaterial, titulo.concat(descricao));

		}

		vo.setMaterial(listaMaterial.toString()); // Seta MATERIAL

	}

	/*
	 * Métodos utilitários
	 */

	/**
	 * Popula um StringBuffer no formato de uma lista de valores
	 * 
	 * @param listaValores
	 * @param titulo
	 * @param valor
	 */
	public void popularListaValores(StringBuffer listaValores, Object valor) {
		this.popularListaValores(listaValores, null, valor);
	}

	/**
	 * Popula um StringBuffer no formato de uma lista de valores considerando um valor descritivo para primeiro item
	 * 
	 * @param listaValores
	 * @param tituloPrimeiroItem
	 * @param valor
	 */
	public void popularListaValores(StringBuffer listaValores, String tituloPrimeiroItem, Object valor) {

		if (listaValores == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado");
		}

		if (valor != null) {

			if (listaValores.length() == 0 && StringUtils.isNotEmpty(tituloPrimeiroItem)) {
				listaValores.append(tituloPrimeiroItem);
			}

			listaValores.append(valor.toString().concat("\n"));
		}

	}

	/**
	 * Converte booleano para literal Sim/Não
	 * 
	 * @param valor
	 * @return
	 */
	public String converterBooleanoSimNao(Boolean valor) {
		return valor != null ? (valor ? "SIM" : "NÃO") : "";
	}

	/**
	 * Obtém a idade do paciente
	 * 
	 * @param dataNascimentoPaciente
	 * @return
	 */
	public int calcularIdadePaciente(Date dataNascimentoPaciente) {
		final int anoAtual = Integer.valueOf(this.formataDatas(new Date(), YYYY));
		final int anoNascimento = Integer.valueOf(this.formataDatas(dataNascimentoPaciente, YYYY));
		return anoAtual - anoNascimento;
	}

	/**
	 * Obtém o lado da cirurgia
	 * 
	 * @param ladoCirurgiaAgenda
	 * @return
	 */
	public String obterLadoCirurgia(DominioLadoCirurgiaAgendas ladoCirurgiaAgenda) {
		if (ladoCirurgiaAgenda != null) {
			switch (ladoCirurgiaAgenda) {
			case D:
				return "Lado Direito";
			case E:
				return "Lado Esquerdo";
			case B:
				return "Bilateral";
			default:
				break;
			}
		}
		return "";
	}

	/**
	 * Obtém o diagnóstico
	 * 
	 * @param ladoCirurgiaAgenda
	 * @return
	 */
	public String obterDiagnostico(AghCid aghCid) {
		if (aghCid == null) {
			return "";
		}
		return "Diag: " + aghCid.getCodigo() + " -  " + CoreUtil.nvl(aghCid.getDescricaoEditada(), aghCid.getDescricao());
	}
	
	/**
	 * Formata datas das cirurgias
	 * @param data
	 * @param formato
	 * @return
	 */
	public String formataDatas(Date data, String formato){
		SimpleDateFormat sdf = new SimpleDateFormat(formato);
		if(data != null){
			return sdf.format(data);
		}
		return "";
	}

	private MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	private MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	private EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}

	private MbcAgendaDiagnosticoDAO getMbcAgendaDiagnosticoDAO() {
		return mbcAgendaDiagnosticoDAO;
	}

	private MbcMatOrteseProtCirgDAO getMbcMatOrteseProtCirgDAO() {
		return mbcMatOrteseProtCirgDAO;
	}

	private MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	private MbcEquipamentoUtilCirgDAO getMbcEquipamentoUtilCirgDAO() {
		return mbcEquipamentoUtilCirgDAO;
	}

	private MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}

	private MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}

	private MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
}
