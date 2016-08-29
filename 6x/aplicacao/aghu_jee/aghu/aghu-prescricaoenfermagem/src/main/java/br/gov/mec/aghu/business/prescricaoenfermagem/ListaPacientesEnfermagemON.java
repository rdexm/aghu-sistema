package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeNotificacaoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.VListaEpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PacienteEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PacienteEnfermagemVO.StatusPrescricaoEnfermagemPaciente;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PacienteEnfermagemVO.StatusSinalizadorUP;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ListaPacientePrescricaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.dao.VRapPessoaServidorDAO;
import br.gov.mec.aghu.view.VListaEpePrescricaoEnfermagem;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ListaPacientesEnfermagemON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ListaPacientesEnfermagemON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IControlePacienteFacade controlePacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@Inject
	private VListaEpePrescricaoEnfermagemDAO vListaEpePrescricaoEnfermagemDAO;
	
	@Inject
	private EpeNotificacaoDAO epeNotificacaoDAO;
	
	@Inject
	private VRapPessoaServidorDAO vRapPessoaServidorDAO;

	private static final long serialVersionUID = -767802933726437762L;
	
	public List<PacienteEnfermagemVO> pesquisarListaPacientes(RapServidores servidor) throws BaseException {
		if (servidor == null) {
			throw new IllegalArgumentException("Argumento inválido.");
		}
		servidor = this.getRegistroColaboradorFacade().buscaServidor(servidor.getId());
		
		final List<CseCategoriaProfissional> categorias = getCascaFacade().pesquisarCategoriaProfissional(servidor);
		CseCategoriaProfissional categoria = null;

		if (!categorias.isEmpty()) {
			categoria = categorias.get(0);
		}	
		
		List<VListaEpePrescricaoEnfermagem> listaPrescricaoEnfermagem = vListaEpePrescricaoEnfermagemDAO.listarPacientesEnfermagemView(servidor);

		return montaListaPacienteEnfermagem(listaPrescricaoEnfermagem, categoria);
	}

	private List<PacienteEnfermagemVO> montaListaPacienteEnfermagem(List<VListaEpePrescricaoEnfermagem> listaEPE, CseCategoriaProfissional categoria) throws BaseException {
		List<PacienteEnfermagemVO> listaPacientesEnfermagem = new ArrayList<PacienteEnfermagemVO>();
		final Integer catSeq = categoria != null ? categoria.getSeq() : null;
		final Integer pCatefProfEnf = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_ENF);
		final Integer pCatefProfMed = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
		final Integer pCateOutros = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_OUTROS);
		final AghParametros paramPrevAlta = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_USA_CONTROLE_PREV_ALTA);
		final Short paramControleUP = this.obterParametroControleUP();
		final BigDecimal paramLinhaCorteUP = this.obterParametroLinhaCorteUP();
		
		for (VListaEpePrescricaoEnfermagem view : listaEPE) {
			
			PacienteEnfermagemVO vo = new PacienteEnfermagemVO(view);
			
			vo.setIndPartProjPesquisa(view.getExisteProjetoPaciente());
			vo.setIndSolicConsultoria(false);
			vo.setIndSolicConsultoriaResp(false);
			vo.setIndEvolucao(false);
			vo.setIndPrevAlta(false);
			vo.setIndConsultoriaEnf(view.getSolicitacaoConsultoriaResp());
			vo.setDesabilitaBotaoPrescrever(!view.getHabilitaBotaoPrescricao());
			vo.setDesabilitaBotaoControles(!view.getPacientePossuiControle());
			vo.setDesabilitaBotaoEvolucao(!view.getHabilitaBotaoPrescricao());
			vo.setColorGmr(view.getCorGermMultiResistente());
			vo.setIndGmr(view.getGermMultiResistente());
			vo.setSinalizadorUlceraPressao(this.buscarSinalizadorUP(view.getUlceraPressao(), view.getUlceraMedicao(),  paramControleUP, paramLinhaCorteUP));
			vo.setStatusPrescEnfPaciente(obterIconeStatusPrescricao(view.getServidorPrescricaoVigente(), view.getServidorPrescricaoFutura(), 
					view.getDthrFimPrescricaoVigente(), view.getServidorPrescricaoVigenteValida(), view.getServidorPrescricaoFuturaValida()));
			vo.setIndEvolucao(verificarEvolucao(view, catSeq, pCatefProfEnf, pCatefProfMed, pCateOutros));
			
			if(view.getIndAltaMedica() != null){
				vo.setIndAltaSumario(view.getIndAltaMedica());
			}else{
				vo.setIndAltaSumario(DominioIndConcluido.N);
			}
			
			if (paramPrevAlta.getVlrTexto().equals("S")) {
				verificaPrevAlta(vo, view);
			}
			
			if (view.getSolicitacaoConsultoriaResp()) {
				vo.setIndSolicConsultoriaResp(true);
				vo.setIndSolicConsultoria(false);
			} else {
				if (view.getSolicitacaoConsultoriaSolic()) {
					vo.setIndSolicConsultoria(true);
					vo.setIndSolicConsultoriaResp(false);
				}
			}
				
			listaPacientesEnfermagem.add(vo);
		}

		return listaPacientesEnfermagem;
	}
	
	
	private void verificaPrevAlta(PacienteEnfermagemVO vo, VListaEpePrescricaoEnfermagem view) throws ApplicationBusinessException {
	
		if(DominioOrigemAtendimento.I.equals(view.getOrigem())) {
			if(view.getRespPrevAlta() != null && view.getDtPrevAlta() != null && view.getRespPrevAlta()){
				Integer diferencaDias = DateUtil.diffInDaysInteger(view.getDtPrevAlta(), new Date());
				if(diferencaDias >= 0 && diferencaDias <= 2){
					vo.setColorPrevAlta("#D9FFD9");
					vo.setIndPrevAlta(true);
					return;
				}
			}
		}
		if(DominioOrigemAtendimento.N.equals(view.getOrigem())) {
			if(view.getOrigemAtdMae() != null && view.getOrigemAtdMae().equals(DominioOrigemAtendimento.I.toString())){
				if(view.getRespPrevAltaAtdMae() != null && view.getDtPrevAltaAtdMae() != null && view.getRespPrevAltaAtdMae()){
					Integer diferencaDias = DateUtil.diffInDaysInteger(view.getDtPrevAltaAtdMae(), new Date());
					if(diferencaDias >= 0 && diferencaDias <= 2){
						vo.setColorPrevAlta("#D9FFD9");
						vo.setIndPrevAlta(true);
						return;
					}
				}
			}
		}
	
	}

	public String getDescricaoLocalizacao(AghAtendimentos atendimento) {
		if (atendimento == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}
		return this.getDescricaoLocalizacao(atendimento.getLeito(), atendimento.getQuarto(), atendimento.getUnidadeFuncional());
	}

	public String getDescricaoLocalizacao(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidade) {
		if (leito == null && quarto == null && unidade == null) {
			throw new IllegalArgumentException("Pelo menos um dos argumentos deve ser fornecido.");
		}

		if (leito != null) {
			return String.format("L:%s", leito.getLeitoID());
		}
		if (quarto != null) {
			return String.format("Q:%s", quarto.getDescricao());
		}

		String string = String.format("U:%s %s - %s", unidade.getAndar(), unidade.getIndAla(), unidade.getDescricao());

		return StringUtils.substring(string, 0, 8);
	}

	/**
	 * Retorna informação referente a situação da prescrição do paciente (icone)
	 * 
	 * @param atendimento
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private StatusPrescricaoEnfermagemPaciente obterIconeStatusPrescricao(Integer codigoServidorVigente, Integer codigoServidorFutura, 
			Date dataPrescricaoVigente, Integer codigoServidorVigenteValida, Integer codigoServidorFuturaValida) {
		Boolean existePrescricaoVigente = false;
		Boolean existePrescricaoFutura = false;
		if (codigoServidorVigente != null) {
			existePrescricaoVigente = true;
		}

		if (codigoServidorFutura != null) {
			existePrescricaoFutura = true;
		}

		if (!existePrescricaoVigente) {
			return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_NAO_REALIZADA_OU_PENDENTE;
		} else if (existePrescricaoVigente && !existePrescricaoFutura) {
			Date dataAtual = DateUtil.truncaData(new Date());
			Date dataFim = DateUtil.truncaData(dataPrescricaoVigente);
			Boolean mesmaData = false;

			if (dataAtual.equals(dataFim)) {
				mesmaData = true;
			}

			if (mesmaData) {
				if (codigoServidorVigenteValida == null) {
					return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA;
				} else {
					return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA;
				}
			} else {
				if (codigoServidorVigenteValida == null) {
					return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA_SEGUINTE;
				} else {
					return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA_SEGUINTE;
				}
			}
		} else if (existePrescricaoVigente && existePrescricaoFutura) {
			if (codigoServidorVigenteValida != null && codigoServidorFuturaValida == null) {
				return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA_SEGUINTE;
			}
			if (codigoServidorVigenteValida == null && codigoServidorFuturaValida == null) {
				return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_DO_DIA_E_DIA_SEGUINTE_PENDENTES;
			}
			if (codigoServidorVigenteValida != null && codigoServidorFuturaValida != null) {
				return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_VENCE_NO_DIA_SEGUINTE;
			}
			if (codigoServidorVigenteValida == null && codigoServidorFuturaValida != null) {
				return StatusPrescricaoEnfermagemPaciente.PRESCRICAO_ENFERMAGEM_PENDENTE_VENCE_NO_DIA_SEGUINTE;
			}
		}

		return null;
	}

	private boolean verificarEvolucao(VListaEpePrescricaoEnfermagem view, Integer catSeq, Integer pCatefProfEnf, Integer pCatefProfMed, Integer pCateOutros)
			throws BaseException {
		if (pCatefProfEnf.equals(catSeq)) {
			return view.getEvolucaoPeloEnfermeiro();
		} else {
			if (pCatefProfMed.equals(catSeq) && view.getHabilitaBotaoPrescricao()) {
				return view.getEvolucaoPeloMedico();
			} else {
				if (pCateOutros.equals(catSeq)) {
					return view.getEvolucaoPeloOutros();
				}
			}
		}
		return false;
	}


	/**
	 * Realiza a busca do parâmetro que informa o seq do Item de Controle de Úlcera de Pressão;
	 * 
	 * @return
	 * @throws BaseException
	 */
	private Short obterParametroControleUP() throws BaseException {
		AghParametros aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONTROLES_UP);

		return (aghParametro != null && aghParametro.getVlrNumerico() != null) ? aghParametro.getVlrNumerico().shortValue() : null;
	}

	/**
	 * Realiza a busca do parâmetro que informa o valor de corte para determiniar Úlcera de Pressão;
	 * 
	 * @return
	 * @throws BaseException
	 */
	protected BigDecimal obterParametroLinhaCorteUP() throws BaseException {

		AghParametros aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LINHA_CORTE_UP);

		return aghParametro != null && aghParametro.getVlrNumerico() != null ? aghParametro.getVlrNumerico() : null;
	}
	
	/**
	 * Buscar o sinalizador de úlcera de pressão que servirá para definir o ícone e o tooltip que serão apresentados na lista;
	 * 
	 * @param atendimento
	 * @param paramControleUP
	 * @param paramLinhaCorteUP
	 * @return
	 */
	private StatusSinalizadorUP buscarSinalizadorUP(boolean ulceraPressao, Integer medicao, Short paramControleUP, BigDecimal paramLinhaCorteUP) {

		if (ulceraPressao) {
			return StatusSinalizadorUP.FLAG_VERMELHO;
		}

		if (paramControleUP != null && paramLinhaCorteUP != null) {
			if (medicao == null) {
				return null;
			}
			BigDecimal medicaoB = new BigDecimal(medicao);
			if (medicaoB.compareTo(paramLinhaCorteUP) <= 0) {
				return StatusSinalizadorUP.FLAG_AMARELO;
			} else {
				return StatusSinalizadorUP.FLAG_VERDE;
			}

		}
		return null;
	}
	
	public List<ListaPacientePrescricaoVO> retornarListaImpressaoPrescricaoEnfermagem(List<PacienteEnfermagemVO> listaPacientesEnfermagem) {
		ArrayList<ListaPacientePrescricaoVO> listaPacientesImpressao = new ArrayList<ListaPacientePrescricaoVO>();
		
		for(PacienteEnfermagemVO pacienteEnf : listaPacientesEnfermagem) {
			ListaPacientePrescricaoVO pacImp = new ListaPacientePrescricaoVO();
			pacImp.setLocal(pacienteEnf.getLocal());
			pacImp.setPacNome(pacienteEnf.getNome());
			if(pacienteEnf.getIdade()!=null) {
				pacImp.setIdadePaciente(pacienteEnf.getIdade());
			}
			if(pacienteEnf.getProntuario()!=null) {
				pacImp.setProntuario(normalizarProntuarioSomenteNumeros(pacienteEnf.getProntuario()));
			}
			if(pacienteEnf.getAtdSeq()!=null) {
				AghAtendimentos atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(pacienteEnf.getAtdSeq());
				if (atendimento.getServidor()!=null) {
					String nomeUsualServidor = vRapPessoaServidorDAO.obterNomeUsual(atendimento.getServidor().getId().getMatricula(), 
							atendimento.getServidor().getId().getVinCodigo());
					if (nomeUsualServidor != null) {
						pacImp.setNomeUsual(nomeUsualServidor.substring(0, 11));
					}
				}
			}
			if(pacienteEnf.getDataAtendimento()!=null) {
				pacImp.setDataFormatada(DateUtil.obterDataFormatada(pacienteEnf.getDataAtendimento(), "dd/MM/yy"));
				pacImp.setDthrInicio1(DateUtil.calcularDiasEntreDatas(DateUtil.truncaData(pacienteEnf.getDataAtendimento()), DateUtil.truncaData(new Date())));	
			}
			
			listaPacientesImpressao.add(pacImp);
		}
		
		return listaPacientesImpressao;
	}
	
	private Integer normalizarProntuarioSomenteNumeros(String prontuario) {
	    StringBuffer prontuarioNumerico = new StringBuffer();  
	    for (int i = 0; i < prontuario.length(); i++) {
	        if(Character.isDigit(prontuario.charAt(i))) {
	        	prontuarioNumerico.append(prontuario.charAt(i));
	        }
	    }
	    String prontuarioFormatado = prontuarioNumerico.toString();
	    return Integer.parseInt(prontuarioFormatado);
	} 

	
	
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		return epePrescricaoEnfermagemDAO;
	}

	protected IControlePacienteFacade getControlePacienteFacade() {
		return this.controlePacienteFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	public ICascaFacade getCascaFacade() {
		return cascaFacade;
	}
	
	public IExamesLaudosFacade getExamesLaudosFacade() {
		return examesLaudosFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	public EpeNotificacaoDAO getEpeNotificacaoDAO() {
		return epeNotificacaoDAO;
	}

}