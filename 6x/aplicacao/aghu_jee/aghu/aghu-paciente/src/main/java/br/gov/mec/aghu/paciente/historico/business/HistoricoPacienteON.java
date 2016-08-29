package br.gov.mec.aghu.paciente.historico.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPacientesJn;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesHistDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesJnDAO;
import br.gov.mec.aghu.paciente.vo.HistoricoPacienteVO;
import br.gov.mec.aghu.paciente.vo.SituacaoPacienteVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * O propósito desta classe é prover os métodos de negócio para a consulta de
 * histórico de pacientes.
 */
@Stateless
public class HistoricoPacienteON extends BaseBusiness {


	private static final Log LOG = LogFactory.getLog(HistoricoPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -6543996907531982311L;


	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade; 

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade; 

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private HistoricoPacienteRN historicoPacienteRN;
	
	@Inject
	private AipPacientesHistDAO aipPacientesHistDAO;
	

	@Inject 
	private AipPacientesJnDAO aipPacientesJnDAO;
	
	
	/**
	 * FIXME Verificar com o pessoal de Brasilia se esse enum deve ficar nessa
	 * classe. Enumeracao com as siglas utilizadas para determinar quais
	 * palavras em uma sequencia de palavras que não devem ser alteradas pelo
	 * metodo editaNomes
	 * 
	 */
	private enum SiglasEditaNome {
		I("I"), II("II"), IV("IV"), V("V"), VI("VI"), IX("IX"), XI("XI"), RS(
				"RS"), SC("SC"), PR("PR"), SP("SP"), RJ("RJ"), BA("BA"), MA(
				"MA"), HCPA("HCPA"), PUCRS("PUCRS"), UFRGS("UFRGS"), UFPEL(
				"UFPEL"), UFRJ("UFRJ"), IAHCS("IAHCS"), LTDA("LTDA"), SA("S/A"), ENAP(
				"ENAP"), FDRH("FDRH"), POA("POA"), FACEM("FACEM"), FAFIMC(
				"FAFIMC"), FAPA("FAPA"), FARGS("FARGS"), UNIJUI("UNIJUI"), CIERGS(
				"CIERGS"), SENAI("SENAI"), SENAC("SENAC"), SEBRAE("SEBRAE"), FFFCMPA(
				"FFFCMPA"), MEC("MEC");

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		String siglaString;

		private SiglasEditaNome(String sigla) {
			this.siglaString = sigla;
		}

		static List<String> pesquisarListaSiglasNomeString() {
			List<String> siglasString = new ArrayList<String>();

			SiglasEditaNome[] arraySiglas = SiglasEditaNome.values();
			List<SiglasEditaNome> listaSiglas = Arrays.asList(arraySiglas);
			for (SiglasEditaNome sigla : listaSiglas) {
				siglasString.add(sigla.siglaString);
			}
			return siglasString;
		}
	}

	/**
	 * Obtém as informações do histórico do paciente.
	 * 
	 * @param prontuario
	 *            prontuário do paciente.
	 * @param codigo
	 *            código do paciente.
	 * @param buscarUltEventos
	 *            informa se deve buscar os últimos eventos do paciente.
	 * @param buscarSitAnt
	 *            informa se deve buscar as situações anteriores do paciente.
	 * @return histórico do paciente.
	 * @throws ApplicationBusinessException
	 */
	public HistoricoPacienteVO obterHistoricoPaciente(Integer prontuario,
			Integer codigo, boolean buscarUltEventos, boolean buscarSitAnt)
			throws ApplicationBusinessException {
		HistoricoPacienteVO historicoVO = null;

		AipPacientes pac = this.getPacienteFacade().obterPacientePorCodigoOuProntuario(
				prontuario, codigo, null);
		if (pac != null) {
			historicoVO = new HistoricoPacienteVO();
			historicoVO.setAipPaciente(pac);

			if (buscarUltEventos) {
				buscarUltimosEventos(historicoVO);
			}

			if (buscarSitAnt) {
				obterSituacoesAnteriores(historicoVO);
			}
		}

		return historicoVO;
	}
	
	/**
	 * Obtém as informações dos últimos eventos para um determinado paciente.
	 * 
	 * @param historicoVO
	 *            VO que irá receber os dados.
	 */
	private void buscarUltimosEventos(HistoricoPacienteVO historicoVO) {
		obterLocalDataUltExame(historicoVO);
		obterLocalDataUltProcedimento(historicoVO);
		obterLocalInternacaoLocalAlta(historicoVO);
		obterLocalDataUltimaConsulta(historicoVO);
	}

	/**
	 * Obtém as situações anteriores do paciente.
	 * 
	 * @param historicoVO
	 *            VO que irá receber os dados.
	 */
	@SuppressWarnings("unchecked")
	private void obterSituacoesAnteriores(HistoricoPacienteVO historicoVO) {
		List<AipPacientesJn> pacJns = this.getAipPacientesJnDAO().obterSituacoesAnteriores(
				historicoVO.getAipPaciente().getCodigo());
		List<SituacaoPacienteVO>  situacoes = new ArrayList<SituacaoPacienteVO>();
		for (AipPacientesJn pacJn : pacJns) {
			SituacaoPacienteVO vo = new SituacaoPacienteVO(pacJn.getProntuario(), pacJn.getNomePaciente(),
					pacJn.getDataAlteracao(), pacJn.getDtNascimento(), pacJn.getCodigoCentroCustoCadastro(),
					pacJn.getCodigoCentroCustoRecadastro(), pacJn.getMatriculaServidorCadastro(),
					pacJn.getMatriculaServidorRecadastro(), pacJn.getVinCodigoServidorCadastro(),
					pacJn.getVinCodigoServidorRecadastro(), getNomeServidorPorUserName(pacJn.getNomeUsuario()));
			situacoes.add(vo);
		}
		// Busca o 'local' da situação anterior
		// FIXME Otimizar para tentar usar in no criteria e já trazer em uma
		// unica execução todos os centros de custos por codigo. É melhor
		// separar o resultado depois no servidor de app do que onerar o BD com
		// várias queries
		// FIXME Fazer a query para retornar apenas descricao e codigo do centro
		// de custos e colocar em um MAP para depois distribuir na VO. Não faz
		// sentido carregar todo um objeto do BD para ler apenas a descrição
		// dele. Usar projections para isso.
		// FIXME Usar a Restriction.or para tratar o caso do IF direto na query.
		// Ex.: ( (aipPacienteJn.codigoRecadastro is null and
		// aipPacienteJn.cctCodigoCadastro = fccCentroCustos.codigo) or
		// (aipPacienteJn.codigoRecadastro is not null and
		// aipPacienteJn.codigoRecadastro = fccCentroCustos.codigo) )
		ICentroCustoFacade centroCustoFacade = getCentroCustoFacade();
	
		for (SituacaoPacienteVO situacaoPacienteVO : situacoes) {
			FccCentroCustos centro = centroCustoFacade.pesquisaCentroCustoPorSituacaoPacienteVO(situacaoPacienteVO);		
			if( centro != null){
				situacaoPacienteVO.setLocalSitAnt(centro.getDescricao());
			}
		}

		// Busca o 'informado por' da situação anterior
		// FIXME Otimizar para tentar usar in no criteria e já trazer em uma
		// unica execução todos os servidores por código. É melhor separar o
		// resultado depois no servidor de app do que onerar o BD com várias
		// queries
		// FIXME Fazer a query para retornar apenas nome e codigo do servidor e
		// colocar em um MAP para depois distribuir na VO. Não faz sentido
		// carregar todo um objeto do BD para ler apenas a descrição dele. Usar
		// projections para isso.
		// FIXME Usar a Restriction.or para tratar o caso do IF direto na query
		
		IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
		for (SituacaoPacienteVO situacaoPacienteVO : situacoes) {
			RapServidores servidor = registroColaboradorFacade.pesquisaRapServidorPorSituacaoPacienteVO(situacaoPacienteVO);
			situacaoPacienteVO.setInformadoPor(servidor.getPessoaFisica().getNome());
		}

		historicoVO.setSituacoes(situacoes);
	}

	private String getNomeServidorPorUserName(String nomeUsuario) {
		RapServidores servidor;
		try {
			servidor = registroColaboradorFacade.obterServidorPorUsuario(nomeUsuario);
			if (servidor!=null && servidor.getPessoaFisica() != null){
				return servidor.getPessoaFisica().getNome();
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
		}
		return null;
	}

	/**
	 * Obtém o local e a data do último procedimento realizado pelo paciente.
	 * 
	 * @param historicoVO
	 *            VO que irá receber as informações.
	 */
	private void obterLocalDataUltProcedimento(HistoricoPacienteVO historicoVO) {
		/**
		 * Cirurgias.
		 */
		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		List<MbcCirurgias> cirurgias = blocoCirurgicoFacade.listarCirurgiasPorHistoricoPacienteVO(historicoVO);

		if (cirurgias.size() > 0) {
			historicoVO.setLocalUltProcedimento(obterDescUnidade(cirurgias.get(
					0).getUnidadeFuncional().getSeq(), null, null));
			historicoVO.getAipPaciente().setDtUltProcedimento(
					cirurgias.get(0).getDataInicioCirurgia());
		}
	}

	/**
	 * Obtém o local e a data do último exame realizado pelo paciente.
	 * 
	 * @param historicoVO
	 *            VO que irá as informações de data e local do exame.
	 */
	@SuppressWarnings("unchecked")
	private void obterLocalDataUltExame(HistoricoPacienteVO historicoVO) {
		List result = this.getAghuFacade().listarLocalDataUltExamePorHistoricoPacienteVO(historicoVO);
		
		if (result != null && result.size() > 0) {
			Object[] columns = (Object[]) result.get(0);
			historicoVO.setDtUltExame((Date) columns[3]);
			String ala = ((AghAla) columns[1]).getDescricao();
			if (ala == null) {
				ala = "";
			}
			historicoVO.setLocalUltExame(this.editarNomes(((Byte) columns[0])
					+ " " + ala + " - " + (String) columns[2]));
			// historicoVO.setLocalUltExame(String.valueOf((Byte) columns[0]) +
			// " " + (String) columns[1] + " - " + (String) columns[2]);
		}
	}

	/**
	 * Obtém último local de internação e local de alta realizados pelo
	 * paciente.
	 * 
	 * @param historicoVO
	 *            VO o qual deve receber as informações de local de internação e
	 *            local de alta.
	 * 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void obterLocalInternacaoLocalAlta(HistoricoPacienteVO historicoVO) {
		Short numeroQuarto = null;

		AipPacientes paciente = historicoVO.getAipPaciente();

		if (paciente.getQuarto() != null) {
			numeroQuarto = paciente.getQuarto().getNumero();
		}

		Date dataUltimaInternacao = null;
		boolean buscarUnidadeInternacao = false;
		if (paciente.getDtUltInternacao() != null) {
			dataUltimaInternacao = paciente.getDtUltInternacao();
			buscarUnidadeInternacao = true;
		} else if (paciente.getDtUltAtendHospDia() != null) {
			dataUltimaInternacao = paciente.getDtUltAtendHospDia();
			buscarUnidadeInternacao = true;
		}
		if (buscarUnidadeInternacao) {
			Short unidadeFuncionalSeq = paciente.getUnidadeFuncional() == null ? null
					: paciente.getUnidadeFuncional().getSeq();
			String leitoId = paciente.getLeito() == null ? null : paciente
					.getLeito().getLeitoID();

			Short vUnf = obterUnidadeInternacao(leitoId, numeroQuarto,
					unidadeFuncionalSeq);

			if (vUnf != null) {
				historicoVO.setLocalUltInternacao(obterDescUnidade(vUnf,
						leitoId, numeroQuarto));
			}
		}

		Date dataUltimaAlta = null;
		boolean buscarUnidadeAlta = false;
		if (paciente.getDtUltAlta() != null) {
			dataUltimaAlta = paciente.getDtUltAlta();
			buscarUnidadeAlta = true;
		} else if (paciente.getDtUltAltaHospDia() != null) {
			dataUltimaAlta = paciente.getDtUltAltaHospDia();
			buscarUnidadeAlta = true;
		}

		if (buscarUnidadeAlta) {
			AghAtendimentos atendVolta = null;
			if (dataUltimaAlta.before(dataUltimaInternacao)) {
				atendVolta = obterUnidadeAlta(paciente.getCodigo(), paciente
						.getDtUltInternacao());
			}
			if (atendVolta != null
					&& atendVolta.getUnidadeFuncional().getSeq() != 0) {
				
//				String leitoId = null;
				//Obtém a última internação (não vigente) do paciente
//				AinInternacao ultimaInternacao = this.getInternacaoFacade().obterUltimaInternacaoPaciente(paciente.getCodigo());
//				if (ultimaInternacao != null){
//					if (ultimaInternacao.getLeito() != null){
//						leitoId = ultimaInternacao.getLeito().getLeitoID();
//					}
//				}

				historicoVO.setLocalUltAlta(obterDescUnidade(atendVolta
						.getUnidadeFuncional().getSeq(), atendVolta
						.getLeito() == null ? null : atendVolta.getLeito().getLeitoID(), atendVolta
						.getQuarto() == null ? null : atendVolta.getQuarto().getNumero()));
			}
		}
	}

	/**
	 * Busca o atendimento que corresponde a ultima alta do paciente
	 * 
	 * @param codigo
	 *            do paciente
	 * @param data
	 *            da ultima internacao
	 * @return AghAtendimentos que corresponde a ultima alta do paciente
	 */
	private AghAtendimentos obterUnidadeAlta(Integer codigo,
			Date dataUltInternacao) {
		List<AghAtendimentos> listaAtendimentosVolta = this.getAghuFacade().obterUnidadesAlta(codigo, dataUltInternacao,null);
		
		AghAtendimentos atendimentoVolta = null;
		if (listaAtendimentosVolta != null && listaAtendimentosVolta.size() > 0) {
			atendimentoVolta = (AghAtendimentos) listaAtendimentosVolta.get(0);
		}
		return atendimentoVolta;
	}

	/**
	 * Busca unidade de internacao do paciente
	 * 
	 * @param ltoLtoId
	 *            - leito
	 * @param qrtNumero
	 *            - numero do quarto
	 * @param unfSeq
	 *            - codigo que identifica uma unidade funcional.
	 * @return numero da unidade de internacao
	 */
	private Short obterUnidadeInternacao(String ltoLtoId, Short qrtNumero,
			Short unfSeq) {
		AinQuartos quartoVolta = null;

		Short vUnf = null;
		if (unfSeq != null) {
			vUnf = unfSeq;
		} else if (qrtNumero != null) {
			quartoVolta = this.getInternacaoFacade().pesquisaQuartoPorNumero(qrtNumero);
			if (quartoVolta != null) {
				vUnf = quartoVolta.getUnidadeFuncional().getSeq();
			}
		} else {
			quartoVolta = this.getInternacaoFacade().pesquisaQuartoPorLeitoID(ltoLtoId);
			if (quartoVolta != null) {
				vUnf = quartoVolta.getUnidadeFuncional().getSeq();
			}
		}
		return vUnf;
	}

	/**
	 * Obtém o local e a data da última consulta realizada pelo paciente.
	 * 
	 * @param historicoVO
	 *            VO que irá receber os dados.
	 * 
	 */
	public void obterLocalDataUltimaConsulta(HistoricoPacienteVO historicoVO) {
		Calendar cAtual = Calendar.getInstance();
		cAtual.setTime(new Date());
		cAtual.set(Calendar.SECOND, 0);
		cAtual.set(Calendar.MINUTE, 0);
		cAtual.set(Calendar.HOUR_OF_DAY, 0);

		if (historicoVO.getAipPaciente().getDtUltConsulta() != null) {
			Calendar cUltConsulta = Calendar.getInstance();
			cUltConsulta.setTime((Date) historicoVO.getAipPaciente()
					.getDtUltConsulta().clone());
			cUltConsulta.set(Calendar.SECOND, 0);
			cUltConsulta.set(Calendar.MINUTE, 0);
			cUltConsulta.set(Calendar.HOUR_OF_DAY, 0);

			Date dataUltimaConsulta = null;
			if (cUltConsulta.after(cAtual)) {
				cAtual.set(Calendar.HOUR_OF_DAY, 23);
				cAtual.set(Calendar.MINUTE, 59);

				dataUltimaConsulta = cAtual.getTime();
			} else {
				dataUltimaConsulta = historicoVO.getAipPaciente()
						.getDtUltConsulta();
			}

			List<AacConsultas> listaconsultasVolta = this.getAmbulatorioFacade().pesquisaConsultasPorPacienteDataConsulta(
					historicoVO.getAipPaciente(), dataUltimaConsulta);
			if (listaconsultasVolta.size() > 0) {
				AacConsultas consultaVolta = (AacConsultas) listaconsultasVolta
						.get(0);
				historicoVO.setLocalUltConsulta(this.editarNomes(consultaVolta
						.getGradeAgendamenConsulta()
						.getAacUnidFuncionalSala().getUnidadeFuncional()
						.getDescricao())
						+ " Sala "
						+ consultaVolta.getGradeAgendamenConsulta()
								.getAacUnidFuncionalSala().getId().getSala());
				historicoVO.getAipPaciente().setDtUltConsulta(
						consultaVolta.getDtConsulta());
			} else {
				historicoVO.getAipPaciente().setDtUltConsulta(
						dataUltimaConsulta);
			}
		}
	}

	/**
	 * Método que retorna a descrição da unidade em que o paciente efetuou
	 * exames, procedimentos, consultas, etc.
	 * 
	 * @param ufeUnfSeq
	 *            código que identifica uma unidade funcional.
	 * @param leito
	 *            descrição do leito.
	 * @param quarto
	 *            descrição do quarto.
	 * @return retorna a descrição da unidade.
	 */
	private String obterDescUnidade(Short ufeUnfSeq, String leito, Short quarto) {
		AghUnidadesFuncionais unidade = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(ufeUnfSeq);
		
		StringBuffer descricao = new StringBuffer();
		
		if (unidade != null) {
			descricao.append(unidade.getLPADAndarAlaDescricao());
		}
		
		if (leito != null) {
			if (descricao.length() != 0) {
				descricao.append(' ');
			}
			descricao.append(leito);
		} else if (quarto != null) {
			if (descricao.length() != 0) {
				descricao.append(' ');
			}
			descricao.append(quarto);
		}

		return this.editarNomes(descricao.toString());
	}

	/**
	 * FIXME Provavelmente este metodo sairá desta classe. Falar com pessoal de
	 * Brasilia. Metodo que seta para maiuscula a primeira letra de cada
	 * palavra, de uma sequencia de palavras, exceto para as seguintes siglas
	 * (que se encontram no enum SiglasEditaNome)
	 * "I","II","IV","V","VI","IX","XI","RS","SC","PR","SP","RJ","BA","MA"
	 * "HCPA","PUCRS","UFRGS","UFPEL","UFRJ","IAHCS","LTDA","S/A","ENAP",
	 * "FDRH","POA","FACEM","FAFIMC","FAPA","FARGS","UNIJUI","CIERGS",
	 * "SENAI","SENAC","SEBRAE","FFFCMPA","MEC" e para as situacoes "Dr", "Dr."
	 * (de, da, do... devem ter todas as letras em minusculo) e também somente
	 * para a sequencia: "De Luca" (está implementado desta forma na FUNCTION
	 * RAPC_NOME_EDITADO)
	 * 
	 * @param nome
	 *            - string contendo uma ou mais palavras separadas por espaco.
	 * @return string formatada
	 */

	public String editarNomes(String nome) {
		StringBuffer campo = new StringBuffer();
		String[] vetorNomes = nome.split(" ");

		int tamLista = vetorNomes.length;
		int i = 0;
		while (tamLista > 0) {
			String nomeAux = vetorNomes[i];
			tamLista--;
			if (!nomeAux.trim().equalsIgnoreCase("")) {
				if (SiglasEditaNome.pesquisarListaSiglasNomeString().contains(
						nomeAux)) {
					campo.append(nomeAux).append(' ');
				} else {
					if (nomeAux.length() <= 2) {
						if ((nomeAux.equalsIgnoreCase("dr") || (nomeAux
								.length() == 2 && nomeAux.substring(1, 2)
								.equalsIgnoreCase(".")))
								|| (nomeAux.equalsIgnoreCase("de")
										&& tamLista > 0 && vetorNomes[i + 1]
										.equalsIgnoreCase("luca"))) {
							campo.append(nomeAux.substring(0, 1)
									+ nomeAux.substring(1).toLowerCase() + " ");
						} else {
							campo.append(nomeAux.toLowerCase()).append(' ');
						}
					} else {
						campo.append(nomeAux.substring(0, 1)
								+ nomeAux.substring(1).toLowerCase() + " ");
					}
				}
			}
			i++;
		}
		return campo.toString().replaceAll("\\s+$", "");// Remove espacos em
		// branco somente do
		// lado direito da
		// string;
	}

	public AipPacientesHist obterHistoricoPaciente(Integer prontuario, Integer codigo) {
		return this.getAipPacientesHistDAO().obterHistoricoPaciente(prontuario, codigo);
	}

	/**
	 * Método para persistir um objeto AipPacientesHistorico, gerando um journal
	 * para a operação.
	 * 
	 * @param historicoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void persistirHistoricoPaciente(AipPacientesHist historicoPaciente) throws ApplicationBusinessException  {
		this.getHistoricoPacienteRN().atualizarPacienteHist(historicoPaciente);
	}
	/**
	 * Método para buscar a quantidade de pacientes excluídos existentes no
	 * histórico de pacientes.
	 * 
	 * @param codigo
	 * @param prontuario
	 * @param nome
	 * @return Integer
	 */
	public Long obterHistoricoPacientesExcluidosCount(Integer codigo,
			Integer prontuario, String nome) {
		return this.getAipPacientesHistDAO().obterHistoricoPacientesExcluidosCount(codigo, prontuario, nome);
	}

	/**
	 * Método para pesquisar pacientes excluídos existentes no histórico de
	 * pacientes.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param codigo
	 * @param prontuario
	 * @param nome
	 * @return List<AipPacientesHist>
	 */
	public List<AipPacientesHist> pesquisarHistoricoPacientesExcluidos(Integer firstResult, Integer maxResult, Integer codigo,
			Integer prontuario, String nome) {
		return this.getAipPacientesHistDAO().pesquisarHistoricoPacientesExcluidos(firstResult, maxResult, codigo, prontuario, nome);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected HistoricoPacienteRN getHistoricoPacienteRN() {
		return historicoPacienteRN;
	}
	
	protected AipPacientesHistDAO getAipPacientesHistDAO() {
		return aipPacientesHistDAO;
	}
	
	protected AipPacientesJnDAO getAipPacientesJnDAO() {
		return aipPacientesJnDAO;
	}
	
}
