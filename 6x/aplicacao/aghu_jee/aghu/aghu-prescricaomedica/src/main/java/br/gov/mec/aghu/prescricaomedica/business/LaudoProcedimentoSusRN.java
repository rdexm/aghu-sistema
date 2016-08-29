package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoHospitalarInternoVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioLaudosProcSusVO;
import br.gov.mec.aghu.prescricaomedica.vo.SubRelatorioMudancaProcedimentosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class LaudoProcedimentoSusRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(LaudoProcedimentoSusRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private MpmLaudoDAO mpmLaudoDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5850507664634551652L;

	private static final Integer TAMANHO_MAXIMO_NOME = 40;

	private static final Integer TAMANHO_MAXIMO_DESCRICAO_CID = 200;

	private static final Comparator<SubRelatorioLaudosProcSusVO> JUSTIFICATIVA_COMPARATOR = new Comparator<SubRelatorioLaudosProcSusVO>() {

		@Override
		public int compare(SubRelatorioLaudosProcSusVO o1,
				SubRelatorioLaudosProcSusVO o2) {
			
			if(o1 == null || o2 == null){
				return 0;
			}
			
			if ((o1.getOrdem() != null && o2.getOrdem() != null) && (o1.getOrdem() > o2.getOrdem())) {
				return 1;
			} else if ((o1.getOrdem() !=null && o2.getOrdem() != null) && (o1.getOrdem() < o2.getOrdem())) {
				return -1;
			} else {
				if (o1.getDataHoraInicioValidade() != null
						&& o2.getDataHoraInicioValidade() != null
						&& o1.getDataHoraInicioValidade().before(
								o2.getDataHoraInicioValidade())) {
					return -1;
				} else if (o1.getDataHoraInicioValidade() != null
						&& o2.getDataHoraInicioValidade() != null
						&& o1.getDataHoraInicioValidade().after(
								o2.getDataHoraInicioValidade())) {
					return 1;
				} else {
					if ((o1.getProcedimentoDescricao() != null) && (o2.getProcedimentoDescricao() != null) && !o1.getProcedimentoDescricao().equalsIgnoreCase(
							o2.getProcedimentoDescricao())) {
						return o1.getProcedimentoDescricao()
								.compareToIgnoreCase(
										o2.getProcedimentoDescricao());
					} else {
						if ((o1.getProcedimentoCodigo() != null && o2.getProcedimentoCodigo() != null) && (o1.getProcedimentoCodigo() > o2
								.getProcedimentoCodigo())) {
							return 1;
						} else if ((o1.getProcedimentoCodigo() != null && o2.getProcedimentoCodigo() != null) && (o1.getProcedimentoCodigo() < o2
								.getProcedimentoCodigo())) {
							return -1;
						} else {
							if (o1.getJustificativa() != null
									&& o2.getJustificativa() != null) {
								return o1.getJustificativa()
										.compareToIgnoreCase(
												o2.getJustificativa());
							} else {
								return 0;
							}
						}
					}
				}
			}
		}
	};

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<RelatorioLaudosProcSusVO> pesquisaLaudoProcedimentoSus(
			Integer seqAtendimento, Integer apaSeq, Short seqp, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		RelatorioLaudosProcSusVO relatorioLaudosProcSusVO = null;
		List<RelatorioLaudosProcSusVO> listaRetorno = new ArrayList<RelatorioLaudosProcSusVO>();

		AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(
				seqAtendimento);

		if (atendimento != null) {
			AipPacientes paciente = atendimento.getPaciente();
			VAipEnderecoPaciente enderecoPaciente = getCadastroPacienteFacade()
					.obterEndecoPaciente(paciente.getCodigo());

			relatorioLaudosProcSusVO = new RelatorioLaudosProcSusVO();
			relatorioLaudosProcSusVO.setNomeHospital(getCadastrosBasicosInternacaoFacade()
					.recuperarNomeInstituicaoHospitalarLocal().toUpperCase());
			relatorioLaudosProcSusVO.setCnes(getAghuFacade()
					.recuperarCnesInstituicaoHospitalarLocal());
			relatorioLaudosProcSusVO.setNomePaciente(paciente.getNome());
			relatorioLaudosProcSusVO.setProntuario(paciente.getProntuario());
			relatorioLaudosProcSusVO
					.setLeito(atendimento.getLeito() != null ? atendimento
							.getLeito().getLeitoID() : null);
			if (paciente.getNroCartaoSaude() != null) {
				relatorioLaudosProcSusVO.setCartaoSus(paciente
						.getNroCartaoSaude().toString());
			}
			relatorioLaudosProcSusVO.setNascimento(paciente.getDtNascimento());
			relatorioLaudosProcSusVO
					.setSexo(paciente.getSexo() != null ? paciente.getSexo()
							.getDescricao() : null);
			relatorioLaudosProcSusVO.setNomeMae(paciente.getNomeMae());
			relatorioLaudosProcSusVO.setTelefone(paciente
					.getDddFoneResidencial() != null ? paciente
					.getDddFoneResidencial()
					+ "-" + paciente.getFoneResidencial() : paciente
					.getDddFoneRecado() != null ? paciente.getDddFoneRecado()
					+ "-" + paciente.getFoneRecado() : "  ");
			relatorioLaudosProcSusVO
					.setEndereco(enderecoPaciente.getLogradouro()
							+ (enderecoPaciente.getNroLogradouro() != null ? " "
									+ enderecoPaciente.getNroLogradouro()
									: "")
							+ (enderecoPaciente.getComplLogradouro() != null ? '/' + enderecoPaciente
									.getComplLogradouro()
									: "") + " - "
							+ enderecoPaciente.getBairro());
			relatorioLaudosProcSusVO.setMunicipio(enderecoPaciente.getCidade());
			relatorioLaudosProcSusVO.setIbge(enderecoPaciente.getCodIbge());
			relatorioLaudosProcSusVO.setUf(enderecoPaciente.getUf());
			relatorioLaudosProcSusVO.setCep(enderecoPaciente.getCep());

			String cidPrincipal = getAghuFacade()
					.pesquisarDescricaoCidPrincipal(seqAtendimento, apaSeq, seqp);

			if (cidPrincipal != null) {
				relatorioLaudosProcSusVO
						.setCidPrincipal(cidPrincipal.length() <= TAMANHO_MAXIMO_DESCRICAO_CID ? cidPrincipal
								: cidPrincipal.substring(0,
										TAMANHO_MAXIMO_DESCRICAO_CID));
			}

			List<String> cidsSecundarios = getAghuFacade()
					.pesquisarDescricaoCidSecundario(seqAtendimento, apaSeq, seqp);

			String cidSecundario = null;
			String cidCausas = null;

			if (cidsSecundarios != null && !cidsSecundarios.isEmpty()) {
				cidSecundario = cidsSecundarios.get(0);
				if (cidsSecundarios.size() > 1) {
					cidCausas = cidsSecundarios.get(1);
				}
			}

			if (cidSecundario != null) {
				relatorioLaudosProcSusVO
						.setCidSecundario(cidSecundario.length() <= TAMANHO_MAXIMO_DESCRICAO_CID ? cidSecundario
								: cidSecundario.substring(0,
										TAMANHO_MAXIMO_DESCRICAO_CID));
			}
			if (cidCausas != null) {
				relatorioLaudosProcSusVO
						.setCidCausas(cidCausas.length() <= TAMANHO_MAXIMO_DESCRICAO_CID ? cidCausas
								: cidCausas.substring(0,
										TAMANHO_MAXIMO_DESCRICAO_CID));
			}

			// #53360
			if(servidorLogado!=null) {
				BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO = this
						.buscaConselhoProfissionalServidorVO(servidorLogado);
	
				if (buscaConselhoProfissionalServidorVO != null) {
					// Carregar a partir da função migrada do forms
					relatorioLaudosProcSusVO
							.setNomeSolicitante(buscaConselhoProfissionalServidorVO
									.getNome());
					relatorioLaudosProcSusVO
							.setCpfCnnSolicitante(buscaConselhoProfissionalServidorVO
									.getCpf());
					
					if (buscaConselhoProfissionalServidorVO.getSiglaConselho() != null
							&& buscaConselhoProfissionalServidorVO
									.getNumeroRegistroConselho() != null) {
						relatorioLaudosProcSusVO
								.setRegSolicitante(buscaConselhoProfissionalServidorVO
										.getSiglaConselho()
										+ " "
										+ buscaConselhoProfissionalServidorVO
												.getNumeroRegistroConselho());
					}
				}
			}

			MpmAltaSumario altaSumario = getMpmAltaSumarioDAO()
					.obterAltaSumarioPeloId(seqAtendimento, apaSeq, seqp);
			if (altaSumario != null) {
				relatorioLaudosProcSusVO.setDataSolicitacao(altaSumario
						.getDthrAlta());
			}

			relatorioLaudosProcSusVO
					.setJustificativas(buscaJustificativas(seqAtendimento));
			
			// #40967
			List<SubRelatorioMudancaProcedimentosVO> listMudancaProcedimentos = 
					getFaturamentoFacade().buscarMudancaProcedimentos(seqAtendimento, apaSeq, seqp);
			
			// Se não houver mudança de procedimento, retorna a lista com o relatorioLaudosProcSusVO.
			if (listMudancaProcedimentos.isEmpty()) {
				listaRetorno.add(relatorioLaudosProcSusVO);
				
			} else {
				// Para cada mudança de procedimento retornada, deve gerar um novo relatorioLaudosProcSusVO.
				for (SubRelatorioMudancaProcedimentosVO item : listMudancaProcedimentos) {
					RelatorioLaudosProcSusVO vo = criarVOPreenchido(relatorioLaudosProcSusVO);
					
					vo.setDataSaidaFormatada(item.getDataSaidaFormatada());
					vo.setMudProcSolic(item.getMudProcSolic());
					vo.setMudProcRealiz(item.getMudProcRealiz());
					vo.setMudDescrSolic(item.getMudDescrSolic());
					vo.setMudDescrRealiz(item.getMudDescrRealiz());
						
					listaRetorno.add(vo);
				}
			}
		}
		return listaRetorno;
	}
	
	private RelatorioLaudosProcSusVO criarVOPreenchido(RelatorioLaudosProcSusVO relatorioLaudosProcSusVO) {
		RelatorioLaudosProcSusVO vo = new RelatorioLaudosProcSusVO();
		vo.setNomeHospital(relatorioLaudosProcSusVO.getNomeHospital());
		vo.setCnes(relatorioLaudosProcSusVO.getCnes());
		vo.setNomePaciente(relatorioLaudosProcSusVO.getNomePaciente());
		vo.setProntuario(relatorioLaudosProcSusVO.getProntuario());
		vo.setLeito(relatorioLaudosProcSusVO.getLeito());
		vo.setCartaoSus(relatorioLaudosProcSusVO.getCartaoSus());
		vo.setNascimento(relatorioLaudosProcSusVO.getNascimento());
		vo.setSexo(relatorioLaudosProcSusVO.getSexo());
		vo.setNomeMae(relatorioLaudosProcSusVO.getNomeMae());
		vo.setTelefone(relatorioLaudosProcSusVO.getTelefone());
		vo.setEndereco(relatorioLaudosProcSusVO.getEndereco());
		vo.setMunicipio(relatorioLaudosProcSusVO.getMunicipio());
		vo.setIbge(relatorioLaudosProcSusVO.getIbge());
		vo.setUf(relatorioLaudosProcSusVO.getUf());
		vo.setCep(relatorioLaudosProcSusVO.getCep());
		vo.setCidPrincipal(relatorioLaudosProcSusVO.getCidPrincipal());
		vo.setCidSecundario(relatorioLaudosProcSusVO.getCidSecundario());
		vo.setCidCausas(relatorioLaudosProcSusVO.getCidCausas());
		vo.setNomeSolicitante(relatorioLaudosProcSusVO.getNomeSolicitante());
		vo.setDataSolicitacao(relatorioLaudosProcSusVO.getDataSolicitacao());
		vo.setCpfCnnSolicitante(relatorioLaudosProcSusVO.getCpfCnnSolicitante());
		vo.setRegSolicitante(relatorioLaudosProcSusVO.getRegSolicitante());
		vo.setJustificativas(relatorioLaudosProcSusVO.getJustificativas());
		
		return vo;
	}

	/**
	 * Retorna informações do registro no conselho profissional do servidor
	 * fornecido.<br>
	 * Se null ou chave incompleta, retorna as informações do servidor logado.
	 * 
	 * @param servidor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			RapServidores servidor) throws ApplicationBusinessException  {
		// se servidor null ou chave incompleta
		if (servidor == null || servidor.getId() == null
				|| servidor.getId().getVinCodigo() == null
				|| servidor.getId().getMatricula() == null) {
			return buscaConselhoProfissionalServidorVO(null, null);
		}
		return buscaConselhoProfissionalServidorVO(servidor.getId()
				.getMatricula(), servidor.getId().getVinCodigo());
	}

	/**
	 * Importante: Não será considerada a AÇÃO neste método! A validação de
	 * permissões deve ser feito utilizando os mecanismos criados no AGHU para
	 * validação de permissões.
	 * 
	 * Busca informações do conselho profissional do servidor necessário para
	 * executar uma ação. Caso os parâmetros p_matricula e p_vinculo venham
	 * preenchidos, será considerado o servidor deste vinculo e matricula
	 * informados. Caso tais parâmetros não sejam informados, será considerado o
	 * usuário conectado ao sistema e serão preenchidos (para retornar também)
	 * os parâmetros matricula e vinculo. Será procurado e retornado a primeira
	 * qualificação do servidor associada à ação em ordem alfabética de sigla de
	 * conselho. Por exemplo, se usuário for Médico (CRM) e Dentista (CRO), será
	 * retornado o CRM e seu número.
	 * 
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			Integer matricula, Short vinculo, Boolean testaDataFimVinculo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_VAL_PRESC_CONSELHO_PREF);

		String[] codigoConselhoPrioritario = aghParametros.getVlrTexto().split(
				",");

		BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO = new BuscaConselhoProfissionalServidorVO();

		if (matricula == null || vinculo == null) {
			matricula = servidorLogado.getId().getMatricula();
			vinculo = servidorLogado.getId().getVinCodigo();

			buscaConselhoProfissionalServidorVO.setMatricula(matricula);
			buscaConselhoProfissionalServidorVO.setVinculo(vinculo);

		}

		List<ConselhoProfissionalServidorVO> conselhos = getRegistroColaboradorFacade().buscaConselhosProfissionalServidor(matricula, vinculo, testaDataFimVinculo);
		if (conselhos != null && !conselhos.isEmpty()) {
			ConselhoProfissionalServidorVO conselhoProfissionalServidorVO = null;
			for (ConselhoProfissionalServidorVO conselho : conselhos) {
				for (String codigoConselhoPrioritarioStr : codigoConselhoPrioritario) {
					if (codigoConselhoPrioritarioStr.trim().equals(conselho.getCodigoConselho().toString()) && StringUtils.isNotEmpty(conselho.getNumeroRegistroConselho())) {
						conselhoProfissionalServidorVO = conselho;
						break;
					}
				}
			}

			//Se não achou um conselho preferencial na lista de conselhos do servidor, atribui o 1o conselho.
			if(conselhoProfissionalServidorVO == null){
				conselhoProfissionalServidorVO = conselhos.get(0);
				//Tenta pegar o conselho em que tenha Número de Registro no Conselho
				for (ConselhoProfissionalServidorVO conselho : conselhos) {
					if (StringUtils.isNotEmpty(conselho.getNumeroRegistroConselho())){
						conselhoProfissionalServidorVO = conselho;
						break;
					}
				}
			}
			
			if(conselhoProfissionalServidorVO != null){
				buscaConselhoProfissionalServidorVO.setNome(conselhoProfissionalServidorVO.getNome());
				buscaConselhoProfissionalServidorVO.setCpf(CoreUtil.formataCPF(conselhoProfissionalServidorVO.getCpf()));
				buscaConselhoProfissionalServidorVO.setSiglaConselho(conselhoProfissionalServidorVO.getSiglaConselho());
				buscaConselhoProfissionalServidorVO.setNumeroRegistroConselho(conselhoProfissionalServidorVO.getNumeroRegistroConselho());
		
				if (conselhoProfissionalServidorVO.getNumeroRegistroConselho() != null && conselhoProfissionalServidorVO.getSexo() != null) {
					String titulo = obterTitulosServidor(conselhoProfissionalServidorVO, servidorLogado);
					String nomeServidor = conselhoProfissionalServidorVO.getNome().length() <= TAMANHO_MAXIMO_NOME ? conselhoProfissionalServidorVO
							.getNome() : conselhoProfissionalServidorVO
							.getNome()
							.substring(0, TAMANHO_MAXIMO_NOME);
					
					String nome = titulo + " " + nomeServidor;
					buscaConselhoProfissionalServidorVO.setNome(nome);
				}
			}
		}

		return buscaConselhoProfissionalServidorVO;
	}

	private String obterTitulosServidor(ConselhoProfissionalServidorVO conselhoProfissionalServidorVO, RapServidores servidorLogado) {
		String titulo = null;
		
		if (DominioSexo.M.equals(conselhoProfissionalServidorVO.getSexo())&& conselhoProfissionalServidorVO.getTituloMasculino() != null) {
			titulo = conselhoProfissionalServidorVO.getTituloMasculino();
		} else if (DominioSexo.F.equals(conselhoProfissionalServidorVO.getSexo()) && conselhoProfissionalServidorVO.getTituloFeminino() != null) {
			titulo = conselhoProfissionalServidorVO.getTituloFeminino();
		}
		
		// #53939 - se o usuário logado possui o perfil ENF05, seu título deve ser "Téc. Enf."
		if (cascaFacade.usuarioTemPerfil(servidorLogado.getUsuario(), "ENF05") && !cascaFacade.usuarioTemPerfil(servidorLogado.getUsuario(), "ENF01")){
			titulo = "Téc. Enf.";
		}
		
		return titulo;
	}

	public BuscaConselhoProfissionalServidorVO buscaConselhoProfissionalServidorVO(
			Integer matricula, Short vinculo) throws ApplicationBusinessException {
		
		return buscaConselhoProfissionalServidorVO(matricula, vinculo, Boolean.TRUE);
	}
	
	private List<SubRelatorioLaudosProcSusVO> buscaJustificativas(
			Integer seqAtendimento) throws ApplicationBusinessException {
		List<SubRelatorioLaudosProcSusVO> listaVO;
		List<SubRelatorioLaudosProcSusVO> justificativas = new ArrayList<SubRelatorioLaudosProcSusVO>();

		listaVO = getMpmLaudoDAO().buscarJustificativasLaudoProcedimentoSUS(
				seqAtendimento);
		if (listaVO != null && !listaVO.isEmpty()) {
			justificativas.addAll(listaVO);
		}

		List<ProcedimentoHospitalarInternoVO> listaProcedimentosHospitalaresInternoVO = getMpmLaudoDAO()
				.buscarListaProcedimentosHospitalaresInterno(seqAtendimento);

		if (listaProcedimentosHospitalaresInternoVO != null
				&& !listaProcedimentosHospitalaresInternoVO.isEmpty()) {
			for (ProcedimentoHospitalarInternoVO procedimentoHospitalarInternoVO : listaProcedimentosHospitalaresInternoVO) {
				SubRelatorioLaudosProcSusVO vo = new SubRelatorioLaudosProcSusVO();
				vo.setOrdem(4);

				Integer auxSeq = procedimentoHospitalarInternoVO.getPhiSeq() != null ? procedimentoHospitalarInternoVO
						.getPhiSeq()
						: procedimentoHospitalarInternoVO.getSeq();
				vo.setProcedimentoCodigo(getPrescricaoMedicaFacade()
						.buscaDescricaoProcedimentoHospitalarInterno(auxSeq,
								(short) 1, (byte) 1, (short) 1));
				vo.setProcedimentoDescricao(getPrescricaoMedicaFacade()
						.buscaProcedimentoHospitalarInternoAgrupa(auxSeq,
								(short) 1, (byte) 1, (short) 1));
				vo.setJustificativa(getPrescricaoMedicaFacade()
						.buscaJustificativaItemLaudo(seqAtendimento, auxSeq));

				if (!listaVO.contains(vo)) {
					listaVO.add(vo);
				}
			}
		}
		for (SubRelatorioLaudosProcSusVO item : listaVO) {
			if(!justificativas.contains(item)){
				justificativas.add(item);
			}
		}
		listaVO = getFaturamentoFacade()
				.buscarJustificativasLaudoProcedimentoSUS(seqAtendimento);
		if (listaVO != null && !listaVO.isEmpty()) {
			justificativas.addAll(listaVO);
		}

		Collections.sort(justificativas, JUSTIFICATIVA_COMPARATOR);

		return justificativas;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
