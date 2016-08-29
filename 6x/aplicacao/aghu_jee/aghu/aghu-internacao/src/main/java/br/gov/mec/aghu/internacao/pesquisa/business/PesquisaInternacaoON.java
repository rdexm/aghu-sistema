package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAlaDAO;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacientesAdmitidos;
import br.gov.mec.aghu.dominio.DominioOrigemPaciente;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.internacao.administracao.business.VAinPesqPacOV;
import br.gov.mec.aghu.internacao.business.vo.AtualizarPacienteTipo;
import br.gov.mec.aghu.internacao.business.vo.InternacaoAtendimentoUrgenciaPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AhdHospitaisDiaDAO;
import br.gov.mec.aghu.internacao.dao.AinAcompanhantesInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.CidInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesAdmitidosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesComPrevisaoAltaVO;
import br.gov.mec.aghu.internacao.vo.LeitoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.QuartoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghCapitulosCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAcompanhantesInternacaoId;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class PesquisaInternacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesquisaInternacaoON.class);
	private static final long serialVersionUID = -6753466536388536609L;	
	
	@EJB
	private PesquisaPacienteInternadoON pesquisaPacienteInternadoON;
	
	@EJB
	private PesquisaInternacaoRN pesquisaInternacaoRN;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;
	
	@Inject
	private AhdHospitaisDiaDAO ahdHospitaisDiaDAO;
	
	@Inject
	private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private AghAlaDAO aghAlaDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AinAcompanhantesInternacaoDAO ainAcompanhantesInternacaoDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

	private enum PesquisaInternacaoONExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERIODO_MAX, ERRO_DATA_INICIAL_MENOR_QUE_ATUAL, ERRO_DATA_INICIAL_MENOR_QUE_FINAL, AIN_00275, ERRO_INFORMACAO_TIPO_ALTA_COMPATIVEL, ERRO_PESQUISAR_SEM_SAIDA_POR_PERIODO, AIN_00400, AIN_00401, AIN_00313,
	}

	

	/**
	 * Método responsável por realizar a query de consulta de disponibilidades
	 * de leitos.
	 * 
	 * @param idAcomodacao
	 * @param idClinica
	 * @param seqUnidadeFuncional
	 * @param idLeito
	 * @param numeroQuarto
	 * @return
	 */
	public List<LeitoDisponibilidadeVO> pesquisarDisponibilidadeLeitos(
			final Integer firstResult, final Integer maxResult, final Integer idAcomodacao,
			final Integer idClinica, final Short seqUnidadeFuncional, final String idLeito,
			final Short numeroQuarto) {

		AghUnidadesFuncionais unidadeFuncional = null;
		if(seqUnidadeFuncional != null){
			unidadeFuncional = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
		}
				
		final List<Object[]> resultadoConsulta = getAinLeitosDAO().pesquisarDisponibilidadeLeitos(firstResult, maxResult, idAcomodacao, idClinica, unidadeFuncional, idLeito, numeroQuarto);

		return obterListaLeitoDisponibilidadeVO(resultadoConsulta);
	}
	
	public Long pesquisarDisponibilidadeLeitosCount(
			final Integer idAcomodacao,
			final Integer idClinica, final Short seqUnidadeFuncional, final String idLeito,
			final Short numeroQuarto) {

		AghUnidadesFuncionais unidadeFuncional = null;
		if(seqUnidadeFuncional != null){
			unidadeFuncional = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
		}
				
		return getAinLeitosDAO().pesquisarDisponibilidadeLeitosCount(idAcomodacao, idClinica, unidadeFuncional, idLeito, numeroQuarto);
	}	

	

	/**
	 * Método que recebe o resultado de uma consulta sobre a view de leitos
	 * acomodacoes e retorna uma lista de LeitoDisponibilidadeVO
	 * 
	 * @param criteriaViewLeitoAcomodacoes
	 * @return
	 */
	private List<LeitoDisponibilidadeVO> obterListaLeitoDisponibilidadeVO(final List<Object[]> resultadoConsulta) {

		final List<LeitoDisponibilidadeVO> retorno = new ArrayList<LeitoDisponibilidadeVO>();

		for (final Object[] resultado : resultadoConsulta) {
			final List<Object> valoresResultado = new ArrayList<Object>(Arrays.asList(resultado));
			final List<Object[]> resultadoPesquisaDadosultimoExtrato = getAinExtratoLeitosDAO().obterDadosUltimoExtrato((String) resultado[0]);

			// Essa condição é feita para executar a restrição
			// "vlu.criado_em = ainc_lto_ult_extr(lto.lto_Id)" da view
			// V_AIN_LEITOS_ACOMODACOES. Posteriormente os itens da lista
			// leitoIdRemover são removidos da lista que será apresentada na tela.
			if (!resultadoPesquisaDadosultimoExtrato.isEmpty()) {
				for (final Object[] extrato : resultadoPesquisaDadosultimoExtrato) {
					valoresResultado.addAll(Arrays.asList(extrato));
				}
				retorno.add(this.gerarLeitoDisponibilidadeVO(valoresResultado));
			}			
		}
		return retorno;
	}

	

	/**
	 * método usado para transforma um conjunto de propriedades retornado pela
	 * pesquisa na view de leito ocupação em um LeitoDisponibilidadeVO
	 * 
	 * @param valoresResultado
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private LeitoDisponibilidadeVO gerarLeitoDisponibilidadeVO(List<Object> valoresResultado) {
		final LeitoDisponibilidadeVO vo = new LeitoDisponibilidadeVO();

		vo.setLeitoId((String) valoresResultado.get(0));

		String andar = valoresResultado.get(1) == null ? "0" : valoresResultado.get(1).toString();
		AghAla valor2 = null;
		if (valoresResultado.get(2) != null) {
			valor2 = getAghuFacade().obterAghAlaPorChavePrimaria(valoresResultado.get(2).toString());
		}
		vo.setAlaAndar(andar + "/" + ObjectUtils.toString(valor2));

		final Integer valor3 = valoresResultado.get(3) == null ? null : (Integer) valoresResultado.get(3);
		vo.setClinicaCodigo(valor3);

		final String valor4 = valoresResultado.get(4) == null ? ""	: (String) valoresResultado.get(4);
		vo.setClinicaDescricao(valor4);

		final String valor5 = valoresResultado.get(5) == null ? ""	: (String) valoresResultado.get(5);
		vo.setAcomodacaoDescricao(valor5);

		final DominioSexo valor6 = valoresResultado.get(6) == null ? null : DominioSexo.valueOf(valoresResultado.get(6).toString());
		vo.setSexoOcupacao(valor6);

		final DominioSexoDeterminante valor12 = valoresResultado.get(12) == null ? null	: DominioSexoDeterminante.valueOf(valoresResultado.get(12).toString());
		vo.setSexoDeterminante(valor12);

		final DominioMovimentoLeito valor8 = valoresResultado.get(8) == null ? null : DominioMovimentoLeito.valueOf(valoresResultado.get(8).toString());
		vo.setGrupoMovimentoLeito(valor8);

		final Short valor9 = valoresResultado.get(9) == null ? null : (Short) valoresResultado.get(9);
		vo.setUnidadeFuncionalId(valor9);

		final Short valor7 = valoresResultado.get(7) == null ? null	: (Short) valoresResultado.get(7);
		vo.setNumeroQuarto(valor7);

		// não possui extrato
		if (valoresResultado.size() > 13) {
			vo.setDataHoraLancamento((Date) valoresResultado.get(13));
			final String nomePaciente = (String) valoresResultado.get(16);
			final String justificativaExtrato = (String) valoresResultado.get(14);
			final String descricaoOrigemEventos = (String) valoresResultado.get(15);
			vo.setCriadoEm((Date) valoresResultado.get(17));

			// Não alterar a ordem dessas validações, senão as justificativas
			// são apresentadads erradas na tela
			if (descricaoOrigemEventos != null) {
				vo.setDescricao(descricaoOrigemEventos);
			} else if (nomePaciente != null) {
				vo.setDescricao(nomePaciente);
			} else {
				vo.setDescricao(justificativaExtrato);
			}
		}

		vo.setDescricaoCaracteristica((String) valoresResultado.get(10));
		vo.setAcomodacaoId((Integer) valoresResultado.get(11));

		return vo;
	}

	/**
	 * Método usado na pesquisa de disponibilidade de quartos.
	 */
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<QuartoDisponibilidadeVO> pesquisarDisponibilidadeQuartosVO(final Integer firstResult, final Integer maxResult, final Short nroQuarto,
			final Integer clinica, final Short unidade) {

		final List<AinQuartos> resultList = getAinQuartosDAO().pesquisarDisponibilidadeQuartos(firstResult, maxResult, nroQuarto, clinica,
				unidade);

		final List<QuartoDisponibilidadeVO> listaQuartoDisponibilidadeVO = new ArrayList<QuartoDisponibilidadeVO>(resultList.size());

		for (final AinQuartos quarto : resultList) {
			final QuartoDisponibilidadeVO quartoDisponibilidadeVO = popularQuartoDisponibilidadeVO(quarto);
			listaQuartoDisponibilidadeVO.add(quartoDisponibilidadeVO);
		}

		return listaQuartoDisponibilidadeVO;
	}

	/**
	 * Popula QuartoDisponibilidadeVO com dados de AinQuartos
	 * 
	 * @param quarto
	 * @return
	 */
	private QuartoDisponibilidadeVO popularQuartoDisponibilidadeVO(
			final AinQuartos quarto) {
		final QuartoDisponibilidadeVO quartoDisponibilidadeVO = new QuartoDisponibilidadeVO();
		quartoDisponibilidadeVO.setAla(quarto.getAla() != null ? quarto.getAla().getDescricao() : "");
		quartoDisponibilidadeVO.setClinica(quarto.getClinica());
		quartoDisponibilidadeVO.setQuarto(quarto.getNumero());
		quartoDisponibilidadeVO.setDescricao(quarto.getDescricao());
		quartoDisponibilidadeVO.setSexoDeterminante(quarto
				.getSexoDeterminante());
		quartoDisponibilidadeVO.setSexoOcupacao(quarto.getSexoOcupacao());
		return quartoDisponibilidadeVO;
	}

	/**
	 * Método para retornar a capacidade dos quartos.
	 * 
	 * @param quartosList
	 * @return
	 */
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<QuartoDisponibilidadeVO> pesquisarCapacIntQrt(final List<QuartoDisponibilidadeVO> quartosList) {
		final List<Object[]> result = getAinLeitosDAO().pesquisarCapacIntQrt(quartosList);
		Integer contador = null;
		Short capacInternacao = null;
		Short numero = null;
		QuartoDisponibilidadeVO quarto = null;
		final Iterator<Object[]> iter = result.iterator();
		while (iter.hasNext()) {
			final Object[] obj = iter.next();
			contador = obj[0] == null ? null : Integer.valueOf(obj[0].toString());
			capacInternacao = obj[1] == null ? null : Short.valueOf(obj[1].toString());
			numero = obj[2] == null ? null : Short.valueOf(obj[2].toString());

			quarto = new QuartoDisponibilidadeVO();
			quarto.setQuarto(numero);
			quarto = this.obterQuarto(quartosList, quarto);
			final Integer capacidade = capacInternacao - contador;
			if (quarto != null) {
				quarto.setCapacidade(capacidade);
			}
		}
		return quartosList;

	}

	/**
	 * Método que busca a lista de internações em quartos e leitos.
	 * 
	 * @param quartoList
	 * @return
	 */
	private QuartoDisponibilidadeVO pesquisarPacIntQrt(final List<QuartoDisponibilidadeVO> quartoList) {
		final List<AinInternacao> resultado = getAinInternacaoDAO().pesquisarPacIntQrt(quartoList);
		final QuartoDisponibilidadeVO quartoDisponibilidadeVO = new QuartoDisponibilidadeVO();
		quartoDisponibilidadeVO.setInternacoes(resultado);
		return quartoDisponibilidadeVO;
	}

	/**
	 * Método que busca o total de pacientes internados por quarto.
	 * 
	 * @param quartoList
	 */
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public void pesquisarTotalIntQrt(final List<QuartoDisponibilidadeVO> quartoList) {
		Integer count;
		final List<AinInternacao> internacaoList = this
				.pesquisarPacIntQrt(quartoList).getInternacoes();
		final List<Byte> locaisPacientes = new ArrayList<Byte>();
		final List<Short> quartosIds = new ArrayList<Short>();

		for (final AinInternacao internacao : internacaoList) {
			if (internacao.getIndLocalPaciente().equals(DominioLocalPaciente.Q)) {
				locaisPacientes.add(Byte.valueOf("1"));
			}
			if (internacao.getIndLocalPaciente().equals(DominioLocalPaciente.L)) {
				locaisPacientes.add(Byte.valueOf("2"));
			}
			if (internacao.getLeito() == null) {
				quartosIds.add(internacao.getQuarto().getNumero());
			} else {
				quartosIds.add(internacao.getLeito().getQuarto().getNumero());
			}
		}

		for (final QuartoDisponibilidadeVO quartoDisponibilidadeVO : quartoList) {
			count = 0;
			for (final Short quartoId : quartosIds) {
				if (quartoDisponibilidadeVO.getQuarto().equals(quartoId)) {
					count = count + 1;
				}
				quartoDisponibilidadeVO.setTotalInt(count);
			}
		}
	}

	/**
	 * Busca vagas por quarto.
	 * 
	 * @param quartoList
	 */
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public void pesquisarDispVagasQrt(final List<QuartoDisponibilidadeVO> quartoList) {
		for (final QuartoDisponibilidadeVO quartoDisponibilidadeVO : quartoList) {
			if (quartoDisponibilidadeVO.getSolictLeito() == null
					|| (quartoDisponibilidadeVO.getSolictLeito() != null && quartoDisponibilidadeVO
							.getSolictLeito().equals("P"))) {
				if (quartoDisponibilidadeVO.getCapacidade() != null
						&& quartoDisponibilidadeVO.getTotalInt() != null) {
					int vagas = quartoDisponibilidadeVO.getCapacidade()
							- quartoDisponibilidadeVO.getTotalInt();
					if (vagas == 0) {
						quartoDisponibilidadeVO.setVagas("Lotado");
					} else {
						vagas = quartoDisponibilidadeVO.getCapacidade()
								- quartoDisponibilidadeVO.getTotalInt();
						quartoDisponibilidadeVO.setVagas(Integer.toString(vagas));
					}
				}
			}
		}

	}

	/**
	 * Busca SolicTransfPacientes associadas a quartos e com IndSolicLeito igual
	 * a P
	 * 
	 * @param quartoList
	 * @return
	 */
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<QuartoDisponibilidadeVO> pesquisarTotSolTransPndQrt(final List<QuartoDisponibilidadeVO> quartoList) {

		final List<AinSolicTransfPacientes> resultado = getAinSolicTransfPacientesDAO().pesquisarTotSolTransPndQrt();
		for (final QuartoDisponibilidadeVO quartoDisponibilidadeVO : quartoList) {
			for (final AinSolicTransfPacientes solicTransfPacientes : resultado) {
				if (solicTransfPacientes.getAinQuartos() != null
						&& quartoDisponibilidadeVO.getQuarto().equals(solicTransfPacientes.getAinQuartos().getNumero())) {
					quartoDisponibilidadeVO.setSolictLeito("P");
				}
			}
		}
		return quartoList;
	}


	/**
	 * Método para retornar o objeto de QuartoDisponibilidadeVO, buscado dentro
	 * da lista de quartos recebida por parâmetro.
	 */
	private QuartoDisponibilidadeVO obterQuarto(
			final List<QuartoDisponibilidadeVO> quartoList,
			final QuartoDisponibilidadeVO quarto) {
		for (final QuartoDisponibilidadeVO quartoAux : quartoList) {
			if (quarto.getQuarto().equals(quartoAux.getQuarto())) {
				return quartoAux;
			}
		}
		return null;
	}


	public Long pesquisaPacientesComAltaCount(final Date dataInicial,
			final Date dataFinal, final boolean altaAdministrativa,
			final DominioTipoAlta tipoAlta, final Short unidFuncSeq, final Short espSeq,
			final String tamCodigo) {
		return getAghuFacade().pesquisaPacientesComAltaCount(dataInicial, dataFinal, altaAdministrativa, tipoAlta, unidFuncSeq, espSeq, tamCodigo);

	}

	/***
	 * Realiza a contagem de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * @author Stanley Araujo
	 * @param dataInicial
	 *            - Data inicial para contagem
	 * @param dataFinal
	 *            - Data final para a contagem
	 * @return Quantidade de pacientes com previsão de alta
	 * */
	public Long pesquisaPacientesComPrevisaoAltaCount(final Date dataInicial, final Date dataFinal) {
		return getAinInternacaoDAO().pesquisaPacientesComPrevisaoAltaCount(dataInicial, dataFinal);
	}

	/***
	 * Realiza a pesquisa de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * 
	 * @author Stanley Araujo
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Lista de pacientes com previsão de alta
	 * */
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	@SuppressWarnings("PMD.NPathComplexity")
	public List<PesquisaPacientesComPrevisaoAltaVO> pesquisaPacientesComPrevisaoAlta(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Date dataInicial, final Date dataFinal) {

		final List<PesquisaPacientesComPrevisaoAltaVO> retorno = new ArrayList<PesquisaPacientesComPrevisaoAltaVO>();

		final List<Object[]> res = getAinInternacaoDAO().pesquisaPacientesComPrevisaoAlta(firstResult, maxResult, orderProperty, asc,
				dataInicial, dataFinal);

		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		// Criando lista de VO.
		final Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			final Object[] obj = it.next();
			final PesquisaPacientesComPrevisaoAltaVO vo = new PesquisaPacientesComPrevisaoAltaVO();

			if (obj[0] != null) {

				final String prontAux = ((Integer) obj[0]).toString();
				vo.setProntuarioPaciente(prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (obj[1] != null) {
				vo.setNomePaciente((String) obj[1]);
			}

			if (obj[2] != null) {
				vo.setNomeProfessor((String) obj[2]);
			}

			if (obj[3] != null) {
				final AinLeitos leito = (AinLeitos) obj[3];
				vo.setLeitoID(leito.getLeitoID());
			}

			if (obj[4] != null) {

				vo.setDataInternacao(format.format((Date) obj[4]));
			}

			if (obj[5] != null) {
				vo.setDataPrevisaoAlta(format.format((Date) obj[5]));
			}

			if (obj[6] != null) {
				final AghEspecialidades especialidade = (AghEspecialidades) obj[6];
				vo.setEspecialidadeNomeReduzido(especialidade.getNomeReduzido());
			}
			if (obj[7] != null && ((AinQuartos) obj[7]).getDescricao() != null) {
				vo.setDescricaoQuarto(((AinQuartos) obj[7]).getDescricao());
			}

			if (obj[8] != null && ((AghUnidadesFuncionais) obj[8]).getAndar() != null
					&& ((AghUnidadesFuncionais) obj[8]).getDescricao() != null) {

				final AghUnidadesFuncionais unidadesFuncionais = (AghUnidadesFuncionais) obj[8];

				final String andarAlaDescricao = unidadesFuncionais.getAndarAlaDescricao();

				vo.setUnidadeFuncionalDescricao(andarAlaDescricao);
			}

			retorno.add(vo);
		}

		return retorno;
	}
	
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public List<VAinAltasVO> pesquisaPacientesComAlta(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Date dataInicial, final Date dataFinal, final boolean altaAdministrativa, final DominioTipoAlta tipoAlta,
			final DominioOrdenacaoPesquisaPacComAlta ordenacao, final Short unidFuncSeq, final Short espSeq, final String tamCodigo) {
		return getAghuFacade().pesquisaPacientesComAlta(firstResult, maxResult, orderProperty, asc, dataInicial, dataFinal,
				altaAdministrativa, tipoAlta, ordenacao, unidFuncSeq, espSeq, tamCodigo);
	}

	

	public void validaDefineWhere(final Date dataInicio, final Date dataFim,
			final boolean altaAdministrativa, final DominioTipoAlta tipoAlta,
			final String tamCodigo) throws ApplicationBusinessException {
		final int MAX_DIF = 31;

		if (!dataInicio.before(new Date())) {
			throw new ApplicationBusinessException(
					PesquisaInternacaoONExceptionCode.ERRO_DATA_INICIAL_MENOR_QUE_ATUAL);
		}
		if (dataInicio.after(dataFim)) {
			throw new ApplicationBusinessException(
					PesquisaInternacaoONExceptionCode.ERRO_DATA_INICIAL_MENOR_QUE_FINAL);
		}

		final Calendar auxCal = Calendar.getInstance();
		if (altaAdministrativa) {
			auxCal.setTime(dataInicio);
			auxCal.add(Calendar.DAY_OF_MONTH, MAX_DIF + 1);

			if (dataFim.after(auxCal.getTime())) {
				throw new ApplicationBusinessException(
						PesquisaInternacaoONExceptionCode.ERRO_PERIODO_MAX, MAX_DIF);
			}
		} else {
			auxCal.setTime(dataFim);
			auxCal.set(Calendar.HOUR_OF_DAY, 0);
			auxCal.set(Calendar.MINUTE, 0);
			auxCal.set(Calendar.SECOND, 0);
			auxCal.set(Calendar.MILLISECOND, 0);

			if (dataInicio.before(auxCal.getTime())) {
				throw new ApplicationBusinessException(
						PesquisaInternacaoONExceptionCode.ERRO_PESQUISAR_SEM_SAIDA_POR_PERIODO);
			}
		}

		// OBSERVACAO:Foram utilizados as chaves 'C' e 'D' diretamente no codigo
		// pois elas sao os IDs da classe AinTiposAltaMedica que possui uma
		// string como ID.
		if (DominioTipoAlta.O.equals(tipoAlta)
				&& StringUtils.isNotBlank(tamCodigo)
				&& !tamCodigo.trim().equalsIgnoreCase("C")
				&& !tamCodigo.trim().equalsIgnoreCase("D")) {
			throw new ApplicationBusinessException(
					PesquisaInternacaoONExceptionCode.ERRO_INFORMACAO_TIPO_ALTA_COMPATIVEL);
		}
		if (DominioTipoAlta.E.equals(tipoAlta)
				&& StringUtils.isNotBlank(tamCodigo)
				&& (tamCodigo.trim().equalsIgnoreCase("C") || tamCodigo.trim()
						.equalsIgnoreCase("D"))) {
			throw new ApplicationBusinessException(
					PesquisaInternacaoONExceptionCode.ERRO_INFORMACAO_TIPO_ALTA_COMPATIVEL);
		}

	}

	/**
	 * Pesquisa os pacientes admitidos conforme os parâmetros informado.
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * 
	 * @return Lista de pacientes com previsão de alta
	 * 
	 * */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PesquisaPacientesAdmitidosVO> pesquisaPacientesAdmitidos(final AghEspecialidades codigoEspecialidade,
			final DominioOrigemPaciente origemPaciente, final DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, final AghClinicas codigoClinica,
			final Short codigoConvenio, final Byte codigoPlano, final Date dataInicial, final Date dataFinal, final Integer codigoPaciente, final Integer firstResult,
			final Integer maxResult, final String orderProperty, final boolean asc) {

		final List<PesquisaPacientesAdmitidosVO> listaPesquisaPacientesAdmitidosVO = new ArrayList<PesquisaPacientesAdmitidosVO>();

		final List<Object[]> res = getAinInternacaoDAO().pesquisaPacientesAdmitidos(codigoEspecialidade, origemPaciente, ordenacaoPesquisa, codigoClinica,
				codigoConvenio, codigoPlano, dataInicial, dataFinal, codigoPaciente, firstResult, maxResult, orderProperty, asc);

		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		final SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");

		// Criando lista de VO.
		final Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			final Object[] obj = it.next();
			final PesquisaPacientesAdmitidosVO vo = new PesquisaPacientesAdmitidosVO();

			if (obj[0] != null) {

				final String prontAux = ((Integer) obj[0]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (obj[1] != null) {
				vo.setNomePaciente((String) obj[1]);
			}

			if (obj[2] != null) {
				vo.setNomeProfessor((String) obj[2]);
			}

			if (obj[3] != null) {
				final AinLeitos leito = (AinLeitos) obj[3];
				vo.setLocal(leito.getLeitoID());
			} else if (obj[7] != null && ((AinQuartos) obj[7]).getDescricao() != null) {
				vo.setLocal(((AinQuartos) obj[7]).getDescricao());
			} else if (obj[8] != null && ((AghUnidadesFuncionais) obj[8]).getDescricao() != null) {
				final AghUnidadesFuncionais unidadesFuncionais = (AghUnidadesFuncionais) obj[8];
				vo.setLocal(unidadesFuncionais.getDescricao());
			}

			if (obj[4] != null) {
				vo.setDataInternacao(format.format((Date) obj[4]));
			}

			if (obj[5] != null) {
				vo.setDataAlta(format1.format((Date) obj[5]));
			}
			
			if (obj[6] != null) {

				final String nomeReduzidoEspecialidade = (String) obj[6];
				vo.setNomeEspecialidadeReduzido(nomeReduzidoEspecialidade);
			}

			if (obj[9] != null && obj[10] != null) {
				final String descricaoPlano = (String) obj[9];
				final String descricaoConvenio = (String) obj[10];

				vo.setConvenioPlano(descricaoConvenio + " - " + descricaoPlano);

			}

			/*
			 * if (obj[11] != null) { String descricaoOrigem = (String) obj[11];
			 * String descricaoOrigemTela = null;
			 * 
			 * if (descricaoOrigem.compareTo(DominioOrigemPaciente.A
			 * .getDescricaoBanco()) == 0) { descricaoOrigemTela =
			 * DominioOrigemPaciente.A .getDescricao(); } else if
			 * (descricaoOrigem.compareTo(DominioOrigemPaciente.T
			 * .getDescricaoBanco()) == 0) { descricaoOrigemTela =
			 * DominioOrigemPaciente.T .getDescricao(); } else if
			 * (descricaoOrigem.compareTo(DominioOrigemPaciente.E
			 * .getDescricaoBanco()) == 0) { descricaoOrigemTela =
			 * DominioOrigemPaciente.E .getDescricao(); } else {
			 * 
			 * // * // * Se não assumir os valores de // *
			 * ADMISSAO,TRANFERENCIA,EMERGENCIA então a origem será // *
			 * ADMISSAO // * descricaoOrigemTela = DominioOrigemPaciente.A
			 * .getDescricao(); }
			 * 
			 * vo.setOrigemPaciente(descricaoOrigemTela);
			 * 
			 * }
			 */

			if (obj[13] == null) {
				if (obj[16] == null) {
					vo.setOrigemPaciente(DominioOrigemPaciente.A.getDescricaoBanco());
				} else {
					vo.setOrigemPaciente(DominioOrigemPaciente.E.getDescricaoBanco());
				}
			} else {
				vo.setOrigemPaciente(DominioOrigemPaciente.T.getDescricaoBanco());
			}

			if (obj[12] != null) {
				final AinTiposAltaMedica tam = (AinTiposAltaMedica) obj[12];

				vo.setCodigoTipoAltaMedica(tam.getCodigo());
				vo.setDescricaoTipoAltaMedica(tam.getDescricao());

			}

			if (obj[13] != null) {
				final AghInstituicoesHospitalares ih = (AghInstituicoesHospitalares) obj[13];
				vo.setNomeInstituicaoHospital(ih.getNome());

			}

			if (obj[14] != null) {
				final String descricaoClinia = (String) obj[14];
				vo.setClinica(descricaoClinia);
			}

			if (obj[15] != null) {
				vo.setCodigoPaciente((Integer) obj[15]);
			}
			
			if (obj[17] != null){
				vo.setSeqInternacao((Integer) obj[17]);
			}
			
			if (obj[18] != null) {

				final String nomeEspecialidade = (String) obj[18];
				vo.setNomeEspecialidade(nomeEspecialidade);
			}

			listaPesquisaPacientesAdmitidosVO.add(vo);
		}

		return listaPesquisaPacientesAdmitidosVO;
	}

	/***
	 * Cria um DetachedCriteria de acordo com os parâmetros de pesquisa
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Quantidade de pacientes admitidos
	 * */

	public Long pesquisaPacientesAdmitidosCount(final AghEspecialidades codigoEspecialidade, final DominioOrigemPaciente origemPaciente,
			final DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, final AghClinicas codigoClinica, final Short codigoConveniosPlano,
			final Byte codigoPlano, final Date dataInicial, final Date dataFinal, final Integer codigoPaciente) {

		return getAinInternacaoDAO().pesquisaPacientesAdmitidosCount(codigoEspecialidade, origemPaciente, ordenacaoPesquisa,
				codigoClinica, codigoConveniosPlano, codigoPlano, dataInicial, dataFinal, codigoPaciente);

	}

	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public BigDecimal buscaMatriculaConvenio(final Integer prontuario,
			final Short convenio, final Byte plano) {		
		return getPacienteFacade().buscaMatriculaConvenio(prontuario, convenio, plano);
	}

	public String montaLocalAltaDePaciente(String ltoLtoId, Short qrtNumero, String andar, AghAla indAla) {
		indAla = aghAlaDAO.merge(indAla);
		final StringBuilder local = new StringBuilder();
		if (StringUtils.isBlank(ltoLtoId)) {
			if (qrtNumero != null) {
				local.append("Q:");
				local.append(qrtNumero.toString());
			} else {
				local.append("U:");
				local.append(andar);
				local.append(' ');
				local.append(indAla.toString());
			}
		} else {
			local.append("L:");
			local.append(ltoLtoId);
		}

		return local.toString();
	}

	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public String buscarNomeUsual(final Short pVinCodigo, final Integer pMatricula) {
		return this.getPesquisaInternacaoRN()
				.buscarNomeUsual(pVinCodigo, pMatricula);
	}

	/**
	 * Retorna uma AinInternacao com base na chave primária.
	 * 
	 * @param seq
	 * @return AinInternacao
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacao(final Integer seq) {		
		return getAinInternacaoDAO().obterPorChavePrimaria(seq,null, new Enum[]{ 
				AinInternacao.Fields.PACIENTE,AinInternacao.Fields.LEITO, AinInternacao.Fields.LEITO_QUARTO, AinInternacao.Fields.QUARTO,AinInternacao.Fields.UNIDADE_FUNCIONAL,
				AinInternacao.Fields.ESPECIALIDADE,	AinInternacao.Fields.PROJETO_PESQUISA,AinInternacao.Fields.CONVENIO, AinInternacao.Fields.CONVENIO_SAUDE_PLANO,
				AinInternacao.Fields.SERVIDOR_PROFESSOR, AinInternacao.Fields.SERVIDOR_PROFESSOR_PESSOA_FISICA, AinInternacao.Fields.CARATER_INTERNACAO,AinInternacao.Fields.ORIGEM_EVENTO,
				AinInternacao.Fields.INSTITUICAO_HOSPITALAR, AinInternacao.Fields.TIPO_ALTA_MEDICA});

	}

	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoComLefts(Integer seq) {
		return getAinInternacaoDAO().obterInternacaoComLefts(seq);
	}
	
	/**
	 * Método para buscar a internacação de um paciente que esteja internado
	 * (indPacienteInternado='S'). Caso o paciente não esteja internado,
	 * retornará null.
	 * 
	 * @param codigoPaciente
	 * @return objeto AinInternacao
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPacienteInternado(final Integer codigoPaciente) {
		if (codigoPaciente == null) {
			return null;
		} else {
			return getAinInternacaoDAO().obrterInternacaoPorPacienteInternado(codigoPaciente);
		}
	}

	/**
	 * Método para buscar a última internação de um paciente através do código
	 * do paciente.
	 * 
	 * @param codigoPaciente
	 * @return objeto AinInternacao
	 */
	// @Secure
	public AinInternacao obterInternacaoPacientePorCodPac(final Integer codigoPaciente) {
		return getAinInternacaoDAO().obterInternacaoPacientePorCodPac(codigoPaciente);
	}

	/**
	 * Método que pesquisa as internações de um dado paciente, filtrando pelo
	 * prontuário
	 * 
	 * @param prontuario
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacoesPorProntuarioUnidade(final Integer prontuario) {
		return getAinInternacaoDAO().pesquisarInternacoesPorProntuarioUnidade(prontuario);
	}

	/**
	 * Método para retornar todos objetos de internação de um determinado
	 * paciente
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacaoPorPaciente(final Integer codigoPaciente) {
		return getAinInternacaoDAO().pesquisarInternacaoPorPaciente(codigoPaciente);
	}

	/**
	 * Método para buscar todos registros de internação com o ID do projeto de
	 * pesquisa recebido por parâmetro.
	 * 
	 * @param seqProjetoPesquisa
	 * @return
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacaoPorProjetoPesquisa(final Integer seqProjetoPesquisa) {
		return getAinInternacaoDAO().pesquisarInternacaoPorProjetoPesquisa(seqProjetoPesquisa);
	}

	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Boolean verificarPacienteInternado(final Integer codigoPaciente) {
		return getAinInternacaoDAO().verificarPacienteInternado(codigoPaciente);
	}
	
	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Boolean verificarPacienteHospitalDia(final Integer codigoPaciente) {
		return getAhdHospitaisDiaDAO().verificarPacienteHospitalDia(codigoPaciente);
	}

	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public AinAtendimentosUrgencia obterPacienteAtendimentoUrgencia(final Integer codigoPaciente) {
		return getAinAtendimentosUrgenciaDAO().obterPacienteAtendimentoUrgencia(codigoPaciente);
	}

	/**
	 * Retorna uma view VAinPesqPac com base na prontuario que correspode uma
	 * internação.
	 * 
	 * @param prontuario
	 * @return VAinPesqPac
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public VAinPesqPacOV pesquisaDetalheInternacao(final Integer intSeq) throws ApplicationBusinessException {

		Object[] res = getAinInternacaoDAO().pesquisaDetalheInternacao(intSeq);

		final VAinPesqPacOV vAinPesqPacOV = new VAinPesqPacOV();

		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		if (res != null) {
			if (res[0] != null) {
				vAinPesqPacOV.setSeq((Integer) res[0]);
			}
			if (res[1] != null) {
				vAinPesqPacOV.setPacCodigo((Integer) res[1]);
			}
			if (res[2] != null) {
				final String prontAux = ((Integer) res[2]).toString();
				vAinPesqPacOV.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/"
						+ prontAux.charAt(prontAux.length() - 1));
			}
			if (res[3] != null) {
				vAinPesqPacOV.setNome((String) res[3]);
			}
			if (res[4] != null) {
				vAinPesqPacOV.setDataHoraInternacao((format.format((Date) res[4])));
			}
			if (res[5] != null) {
				vAinPesqPacOV.setDataPrevisaoAlta(((Date) res[5]));
			}
			if (res[6] != null) {
				final Date dt_nascimento = (Date) res[6];
				vAinPesqPacOV.setIdade(calcularIdadePaciente(new Date(), dt_nascimento));
			}
			if (res[7] != null) {
				vAinPesqPacOV.setSexo(((DominioSexo) res[7]).getDescricao().substring(0, 1));
			}
			if (res[8] != null) {
				vAinPesqPacOV.setLtoLtoId(((String) res[8]));
				AinLeitos leito = this.getAinLeitosDAO().obterPorChavePrimaria((String) res[8]);			
				vAinPesqPacOV.setDescricaoQuarto(leito.getQuarto().getDescricao());
				vAinPesqPacOV.setQuartoNumero(leito.getQuarto().getNumero());
			}
			
			if (res[9] != null){
				vAinPesqPacOV.setQuartoNumero((Short) res[9]);
				AinQuartos quarto = this.getAinQuartosDAO().obterPorChavePrimaria((Short) res[9]);
				vAinPesqPacOV.setDescricaoQuarto(quarto.getDescricao());				
			}

			final Short unfSeq = getPesquisaPacienteInternadoON().buscarUnidadeInternacao(vAinPesqPacOV.getLtoLtoId(), (Short) res[9],
					(Short) res[10]);
			vAinPesqPacOV.setUnfSeq(unfSeq);
			final AghUnidadesFuncionais unidadeFuncional = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
			vAinPesqPacOV.setAndarAlaDescricao(unidadeFuncional.getLPADAndarAlaDescricao());

			if (res[11] != null) {
				final FatConvenioSaude convenioSaude = (FatConvenioSaude) res[11];
				vAinPesqPacOV.setCodigoConvenio(convenioSaude.getCodigo());
				vAinPesqPacOV.setDescricaoConvenio(convenioSaude.getDescricao());
			}
			if (res[12] != null) {
				final AghEspecialidades especialidade = (AghEspecialidades) res[12];
				vAinPesqPacOV.setSiglaEspecialidade(especialidade.getSigla());
				vAinPesqPacOV.setNomeEspecialidade(especialidade.getNomeEspecialidade());

			}
			if (res[13] != null && res[14] != null) {

				final Integer matricula = (Integer) res[13];
				final Short vinCodigo = (Short) res[14];

				final String numeroConselho = getPesquisaInternacaoRN().buscarNroRegistroConselho(vinCodigo, matricula);
				vAinPesqPacOV.setNumeroRegistroConselho(numeroConselho);

				final String nomeProfessor = getPesquisaInternacaoRN().buscarNomeUsual(vinCodigo, matricula);
				vAinPesqPacOV.setNomeProfessor(nomeProfessor);
			}
			if (res[15] != null) {
				final AghOrigemEventos origemEventos = (AghOrigemEventos) res[15];
				vAinPesqPacOV.setDescricaoOrigemEventos(origemEventos.getDescricao());
			}
			if (res[16] != null) {
				final AinAtendimentosUrgencia atendimentoUrgencia = (AinAtendimentosUrgencia) res[16];
				vAinPesqPacOV.setDataHoraAntendimentoUrgencia(format.format(atendimentoUrgencia.getDtAtendimento()));
				vAinPesqPacOV.setDataHoraAltaAntendimentoUrgencia(format.format(atendimentoUrgencia.getDtAltaAtendimento()));

				vAinPesqPacOV.setSeqAntendimentoUrgencia(atendimentoUrgencia.getSeq());
			}
		}

		/*
		 * Pesquisa na AinMovimentosInternacao
		 */
		final AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
		final Integer codMvtoInt = parametro.getVlrNumerico().intValue();

		res = getAinMovimentoInternacaoDAO().pesquisaAinMovimentosInternacao(codMvtoInt, vAinPesqPacOV.getSeq());

		if (res != null && res[0] != null) {
			vAinPesqPacOV.setMatriculaServidorGerado((Integer) res[0]);
		}

		if (res != null && res[1] != null) {
			vAinPesqPacOV.setVinculoServidorCodigoGerado((Short) res[1]);

		}

		final RapServidores servidor = this.getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(vAinPesqPacOV.getMatriculaServidorGerado(),
				vAinPesqPacOV.getVinculoServidorCodigoGerado()));
		if (servidor != null) {
			vAinPesqPacOV.setNomeServidorGerado(servidor.getPessoaFisica().getNome());
		}

		return vAinPesqPacOV;

	}

	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<CidInternacaoVO> pesquisaCidsInternacao(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Integer codInternacao) {

		final AinInternacao internacao = getAinInternacaoDAO().obterPorChavePrimaria(codInternacao);

		final List<CidInternacaoVO> listaCid = new ArrayList<CidInternacaoVO>();

		if (internacao != null && internacao.getCidsInternacao() != null && internacao.getCidsInternacao().size() > 0) {
			for (final AinCidsInternacao cidInternacao : internacao.getCidsInternacao()) {
				final CidInternacaoVO cidInternacaoVO = new CidInternacaoVO();
				cidInternacaoVO.setDescricao(cidInternacao.getCid().getDescricao());
				cidInternacaoVO.setPrioridade(cidInternacao.getPrioridadeCid().getDescricao());

				final AghCapitulosCid aghCapitulosCid = getAghuFacade().obterAghCapitulosCidPorChavePrimaria(
						cidInternacao.getCid().getGrupoCids().getId().getCpcSeq());
				cidInternacaoVO.setCapitulo(aghCapitulosCid.getNumero().toString());

				listaCid.add(cidInternacaoVO);
			}
		}
		return listaCid;
	}

	public Long pesquisaCidsInternacaoCount(final Integer codInternacao) {
		int ret = 0;
		final AinInternacao internacao = getAinInternacaoDAO().obterPorChavePrimaria(codInternacao);
		if (internacao != null && internacao.getCidsInternacao() != null) {
			ret = internacao.getCidsInternacao().size();
		}
		return Long.valueOf(ret);
	}

	/**
	 * Metodo que busca internacoes de um paciente atraves do seu prontuario
	 * (obrigatorio) e sua data de internacao (opcional).
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param prontuario
	 *            - prontuario do paciente o qual desejamos buscar as
	 *            internacoes.
	 * @param dataInternacao
	 *            - data de internacao do paciente
	 * @return lista de internacoes do respectivo prontuario informado como
	 *         parametro.
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer prontuario, final Date dataInternacao) {
		return getAinInternacaoDAO().pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(firstResult, maxResult, orderProperty,
				asc, prontuario, dataInternacao);
	}

	public Long pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(final Integer prontuario, final Date dataInternacao) {
		return getAinInternacaoDAO().pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(prontuario, dataInternacao);
	}

	/**
	 * Inner Class com implementação de comparator para ordenar lista de
	 * resultados através dos campos dataInicio (Date) e sigla (String). Esse
	 * comparator é necessário, pois na query não é possível fazer essa
	 * ordenação, uma vez que a sigla não é um campo da tabela ('INT', 'ATU'),
	 * mas um campo com valor no SELECT em duas queries que fazem UNION.
	 */
	class InternacaoAtendimentoUrgenciaComparator implements
			Comparator<InternacaoAtendimentoUrgenciaPacienteVO> {

		@Override
		public int compare(final InternacaoAtendimentoUrgenciaPacienteVO o1,
				final InternacaoAtendimentoUrgenciaPacienteVO o2) {

			final Date data1 = (o1)
					.getDataInicio();
			final Date data2 = (o2)
					.getDataInicio();

			final int comparacaoDatas = data1.compareTo(data2);

			if (comparacaoDatas != 0) {
				return comparacaoDatas;
			} else {
				final String sigla1 = (o1)
						.getSigla().toString();
				final String sigla2 = (o2)
						.getSigla().toString();

				return sigla1.compareTo(sigla2);
			}
		}
	}

	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<InternacaoAtendimentoUrgenciaPacienteVO> pesquisarInternacaoAtendimentoUrgenciaPorPaciente(
			final Integer codigoPaciente) {

		final List<InternacaoAtendimentoUrgenciaPacienteVO> voList = new ArrayList<InternacaoAtendimentoUrgenciaPacienteVO>();

		if (codigoPaciente != null) {
			this.executarQuery1(voList, codigoPaciente);
			this.executarQuery2(voList, codigoPaciente);

			Collections.sort(voList,
					new InternacaoAtendimentoUrgenciaComparator());
			// Faz o "reverse", pois a ordenação é DESC
			Collections.reverse(voList);
		}

		return voList;
	}

	private void executarQuery1(final List<InternacaoAtendimentoUrgenciaPacienteVO> voList, final Integer codigoPaciente) {

		final List<Object[]> lista = getAinInternacaoDAO().pesquisarAtendimentoPorPaciente(codigoPaciente);

		this.popularInternacaoAtendimentoUrgenciaPacienteVO(voList, lista, AtualizarPacienteTipo.INT);
	}

	private void executarQuery2(final List<InternacaoAtendimentoUrgenciaPacienteVO> voList, final Integer codigoPaciente) {

		final List<Object[]> lista = getAinAtendimentosUrgenciaDAO().pesquisarAtendimentoUrgenciaPorPaciente(codigoPaciente);

		this.popularInternacaoAtendimentoUrgenciaPacienteVO(voList, lista, AtualizarPacienteTipo.ATU);
	}

	/**
	 * Método para carregar elementos de uma lista de objetos para uma lista
	 * tipada (InternacaoAtendimentoUrgenciaPacienteVO)
	 * 
	 * @param voList
	 * @param lista
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void popularInternacaoAtendimentoUrgenciaPacienteVO(final List<InternacaoAtendimentoUrgenciaPacienteVO> voList,
			final List<Object[]> lista, final AtualizarPacienteTipo sigla) {
		InternacaoAtendimentoUrgenciaPacienteVO vo = new InternacaoAtendimentoUrgenciaPacienteVO();

		for (final Object[] object : lista) {
			vo = new InternacaoAtendimentoUrgenciaPacienteVO();
			vo.setDataInicio(object[0] == null ? null : (Date) object[0]);
			vo.setLocalPaciente(object[1] == null ? null : (DominioLocalPaciente) object[1]);
			vo.setLeitoId(object[2] == null ? null : (String) object[2]);
			vo.setNumeroQuarto(object[3] == null ? null : (Short) object[3]);
			vo.setSeqUnidadeFuncional(object[4] == null ? null : (Short) object[4]);
			vo.setDataAltaMedica(object[5] == null ? null : (Date) object[5]);
			vo.setSeqAtendimentoUrgencia(object[6] == null ? null : (Integer) object[6]);
			vo.setCodigoTipoAltaMedica(object[7] == null ? null : (String) object[7]);

			vo.setSigla(sigla);

			voList.add(vo);
		}
	}

	/*
	 * Verifica se é possível internar paciente no leito informado em função do
	 * sexo
	 */
	public void consistirSexoLeito(final Integer codigoPaciente, final String idLeito)
			throws ApplicationBusinessException {
		final AipPacientes paciente = getPacienteFacade().obterPacientePorCodigo(codigoPaciente);
		final AinLeitos leito = getCadastrosBasicosInternacaoFacade().obterLeitoInternacaoPorId(idLeito);
		consistirSexoLeito(paciente, leito);
	}

	/*
	 * Verifica se é possível internar paciente no leito informado em função do
	 * sexo
	 */
	public void consistirSexoLeito(final AipPacientes paciente, final AinLeitos leito)
			throws ApplicationBusinessException {
		consistirSexoQuarto(paciente, leito.getQuarto());
	}

	/*
	 * Verifica se é possível internar paciente no quarto informado em função do
	 * sexo
	 */
	public void consistirSexoQuarto(final Integer codigoPaciente, final Short quartoNumero)
			throws ApplicationBusinessException {
		final AipPacientes paciente = getPacienteFacade().obterPacientePorCodigo(codigoPaciente);
		final AinQuartos quarto = getCadastrosBasicosInternacaoFacade().obterQuarto(quartoNumero);
		consistirSexoQuarto(paciente, quarto);
	}

	/*
	 * Verifica se é possível internar paciente no quarto informado em função do
	 * sexo (quando o quarto consiste sexo determinante)
	 */
	public void consistirSexoQuarto(final AipPacientes paciente, final AinQuartos quarto) throws ApplicationBusinessException {
		if(quarto.isConsSexo()) {
			if (!DominioSexoDeterminante.Q.equals(quarto.getSexoDeterminante())) {
				if (!paciente.getSexo().toString().equals(quarto.getSexoDeterminante().toString())) {
					throw new ApplicationBusinessException(PesquisaInternacaoONExceptionCode.AIN_00400);
				}
			}
			if (quarto.getSexoOcupacao() != null&& !quarto.getSexoOcupacao().equals(paciente.getSexo())) {
				throw new ApplicationBusinessException(	PesquisaInternacaoONExceptionCode.AIN_00401);
			}
		}
	}

	/*
	 * Verifica se o quarto pertence a uma unidade de internação ORADB:
	 * Procedure AINP_TRAZ_QRT da AINF_DISP_LEITOS.PLL ORADB: View
	 * V_AIN_QUARTOS_TRANSF
	 */
	public void consistirQuarto(final AinQuartos quarto) throws ApplicationBusinessException {
		if (!quarto.getUnidadeFuncional().getIndUnidInternacao().equals(
				DominioSimNao.S)) {
			throw new ApplicationBusinessException(
					PesquisaInternacaoONExceptionCode.AIN_00313);
		}
	}
	
	
	public boolean verificarExisteInternacao(Integer numeroProntuario)
			throws ApplicationBusinessException {

		return (this.getAinAtendimentosUrgenciaDAO()
				.verificarExisteAtendimentoUrgencia(numeroProntuario) || this
				.getAinInternacaoDAO().verificarExisteInternacaoPaciente(
						numeroProntuario));

	}

	/**
	 * Calcula idade do paciente
	 * 
	 * @param dtObito
	 * @param dtNascimento
	 * @return
	 */
	private Integer calcularIdadePaciente(final Date dtAtual, final Date dtNascimento) {

		final SimpleDateFormat dia = new SimpleDateFormat("dd");
		final SimpleDateFormat mes = new SimpleDateFormat("MM");
		final SimpleDateFormat ano = new SimpleDateFormat("yyyy");

		final Integer idade = Integer.valueOf(ano.format(dtAtual))
				- Integer.valueOf(ano.format(dtNascimento));
		if (Integer.valueOf(mes.format(dtAtual)) == Integer.valueOf(mes
				.format(dtNascimento))) {
			if (Integer.valueOf(dia.format(dtAtual)) > Integer.valueOf(dia
					.format(dtNascimento))) {
				return idade;
			} else if (idade > 0) {
				return idade - 1;
			} else {
				return 0;
			}
		} else if (Integer.valueOf(mes.format(dtAtual)) > Integer.valueOf(mes
				.format(dtNascimento))) {
			return idade;
		} else {
			return idade - 1;
		}
	}

	/**
	 * Retorna os acompanhantes na internação.
	 * 
	 * @param seq
	 *            da Internação.
	 * @return Lista de Acomponhantes
	 */
	@Secure("#{s:hasPermission('acompanhante','pesquisar')}")
	public List<AinAcompanhantesInternacao> obterAcompanhantesInternacao(final Integer seq) {
		return getAinAcompanhantesInternacaoDAO().obterAcompanhantesInternacao(seq);
	}
	
	public Boolean verificarPacienteInternadoPorConsulta(Integer numeroConsulta){
		return ainInternacaoDAO.verificarPacienteInternadoPorConsulta(numeroConsulta);
	}
	
	public Boolean verificaPacienteIngressoSOPorConsulta(Integer numeroConsulta){
		return ainAtendimentosUrgenciaDAO.verificaPacienteIngressoSOPorConsulta(numeroConsulta);
	}
	
	public AinAcompanhantesInternacao obterAinAcompanhantesInternacao(AinAcompanhantesInternacaoId id){
		return getAinAcompanhantesInternacaoDAO().obterPorChavePrimaria(id);
	}
	
	
	protected PesquisaInternacaoRN getPesquisaInternacaoRN(){
		return pesquisaInternacaoRN;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
	
	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}
	
	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO() {
		return ainSolicTransfPacientesDAO;
	}
	
	protected AhdHospitaisDiaDAO getAhdHospitaisDiaDAO() {
		return ahdHospitaisDiaDAO;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}
	
	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}
	
	protected AinAcompanhantesInternacaoDAO getAinAcompanhantesInternacaoDAO() {
		return ainAcompanhantesInternacaoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected PesquisaPacienteInternadoON getPesquisaPacienteInternadoON() {
		return pesquisaPacienteInternadoON;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
}