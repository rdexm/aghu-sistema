package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.constantes.TipoItemAprazamento;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.util.TipoPrescricaoCuidadoEnfermagem;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class ManutencaoPrescricaoEnfermagemON extends BaseBusiness {

	private static final String DOIS_PONTOS_ = ": ";

	private static final String ___ = "   ";

	private static final Log LOG = LogFactory.getLog(ManutencaoPrescricaoEnfermagemON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1884961633092903017L;
	
	public static enum ManutencaoPrescricaoEnfermagemONExceptionCode implements BusinessExceptionCode {
		LOCAL_ATENDIMENTO_NAO_ENCONTRADO, NENHUM_CUIDADO_PARA_IMPRIMIR
	}	
	
	/**
	 * Identifica o nome de cada chave declarada nos arquivos properties para o
	 * resumo do local de atendimento
	 */
	public static enum LocalAtendimento {
		LOCAL_ATENDIMENTO_LEITO, LOCAL_ATENDIMENTO_QUARTO, LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL;
	}

	public PrescricaoEnfermagemVO buscarDadosCabecalhoPrescricaoEnfermagemVO(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId) throws ApplicationBusinessException {
	
		if (prescricaoEnfermagemId == null) {
			throw new IllegalArgumentException(
					"buscarDadosCabecalhoPrescricaoEnfermagemVO: parametros de filtro invalido");
		}

		EpePrescricaoEnfermagem prescricaoEnfermagem = this.getEpePrescricaoEnfermagemDAO()
				.obterPorChavePrimaria(prescricaoEnfermagemId);
		
		if(prescricaoEnfermagem != null && this.getEpePrescricaoEnfermagemDAO().contains(prescricaoEnfermagem)){
			this.getEpePrescricaoEnfermagemDAO().refresh(prescricaoEnfermagem);
		}
		
		if (prescricaoEnfermagem.getDthrMovimento() == null){
			prescricaoEnfermagem.setDthrMovimento(new Date());			
		}
		if (prescricaoEnfermagem.getSituacao() == DominioSituacaoPrescricao.L){
			prescricaoEnfermagem.setSituacao(DominioSituacaoPrescricao.U);
		}

		return popularDadosCabecalhoPrescricaoEnfermagemVO(prescricaoEnfermagem);
	}
	
	/**
	 * Semelhante ao método buscarDadosCabecalhoPrescricaoEnfermagemVO mas não altera a data de movimentação e a situação da prescrição de enfermagem
	 * @param prescricaoEnfermagemId
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public PrescricaoEnfermagemVO buscarDadosCabecalhoPrescricaoEnfermagemUtilizadoPrescricaoMedicaVO(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId) throws ApplicationBusinessException {
	
		if (prescricaoEnfermagemId == null) {
			throw new IllegalArgumentException(
					"buscarDadosCabecalhoPrescricaoEnfermagemVO: parametros de filtro invalido");
		}

		EpePrescricaoEnfermagem prescricaoEnfermagem = this.getEpePrescricaoEnfermagemDAO()
				.obterPorChavePrimaria(prescricaoEnfermagemId);
		
		return popularDadosCabecalhoPrescricaoEnfermagemVO(prescricaoEnfermagem);
	}
	
	/**
	 * @param prescricaoEnfermagemVO
	 *            VO que armazena os dados do cabeçalho.
	 * @param prescricaoEnfermagem
	 *            Contém as informações necessárias para popular os dados do
	 *            cabeçalho no VO.
	 * @throws ApplicationBusinessException
	 */
	public PrescricaoEnfermagemVO popularDadosCabecalhoPrescricaoEnfermagemVO(
			EpePrescricaoEnfermagem prescricaoEnfermagem)
			throws ApplicationBusinessException {

		prescricaoEnfermagem = this.getEpePrescricaoEnfermagemDAO().merge(prescricaoEnfermagem);
		PrescricaoEnfermagemVO prescricaoEnfermagemVO = new PrescricaoEnfermagemVO();
		AghAtendimentos aghAtendimentos = prescricaoEnfermagem.getAtendimento();
		AipPacientes aipPacientes = aghAtendimentos.getPaciente();
		
		if(prescricaoEnfermagem.getAtendimento().getInternacao() != null){
		//acesso lazy
		prescricaoEnfermagem.getAtendimento().getInternacao().getConvenioSaude();
		}
		
		// Dados de EpePrescricaoEnfermagem
		Date dthrInicio = prescricaoEnfermagem.getDthrInicio();
		Date dthrFim = prescricaoEnfermagem.getDthrFim();

		// Dados de AghAtendimentos
		String local = buscarResumoLocalPaciente(aghAtendimentos);

		// Dados de AipPacientes
		Integer prontuario = aipPacientes.getProntuario();
		String nome = aipPacientes.getNome();

		// Popula campos de PrescricaoMedicaVO
		prescricaoEnfermagemVO.setDthrInicio(dthrInicio);
		prescricaoEnfermagemVO.setDthrFim(dthrFim);
		prescricaoEnfermagemVO.setLocal(local);
		prescricaoEnfermagemVO.setProntuario(prontuario);
		prescricaoEnfermagemVO.setNome(nome);
		
		// Prescricao Enfermagem
		prescricaoEnfermagemVO.setPrescricaoEnfermagem(prescricaoEnfermagem);
		
		return prescricaoEnfermagemVO;
	}	
	
	/**
	 * Busca informações do local de atendimento do paciente
	 * 
	 * @param aghAtendimentos
	 * @return Resumo indicando o local de atendimento do paciente.
	 * @throws ApplicationBusinessException
	 *             Lança exceção quando não for identificado o local de
	 *             atendimento do paciente.
	 */
	public String buscarResumoLocalPaciente(AghAtendimentos aghAtendimentos) throws ApplicationBusinessException {
		String local = null;
		
		if(aghAtendimentos == null) {
			throw new IllegalArgumentException("Parâmetro Inválido!");
		}
		
		if (aghAtendimentos.getLeito() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString(),
					aghAtendimentos.getLeito().getLeitoID());
		} else if (aghAtendimentos.getQuarto() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString(),
					aghAtendimentos.getQuarto().getDescricao());
		} else if (aghAtendimentos.getUnidadeFuncional() != null) {
			AghUnidadesFuncionais unidadeFuncional = aghAtendimentos
					.getUnidadeFuncional();
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL
							.toString(), unidadeFuncional
							.getAndarAlaDescricao());
		} else {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoEnfermagemONExceptionCode.LOCAL_ATENDIMENTO_NAO_ENCONTRADO);
		}
		
		return local;
	}
	
		
	public String buscarMensagemLocalizada(String chave, Object... parametros) {
		String mensagem = getResourceBundleValue(chave);

		// Faz a interpolacao de parâmetros na mensagem
		mensagem = MessageFormat.format(mensagem, parametros);

		return mensagem;
	}	
	
	protected EpePrescricaoEnfermagemDAO getEpePrescricaoEnfermagemDAO() {
		return epePrescricaoEnfermagemDAO;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	/**
	 * Pesquisa os Cuidados associados a uma Prescrição de Enfermagem. 
	 * 
	 * @param prescricaoEnfermagemId
	 * @param listarTodas
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<CuidadoVO> buscarCuidadosPrescricaoEnfermagem(
			EpePrescricaoEnfermagemId prescricaoEnfermagemId, Boolean listarTodas) {
					if (prescricaoEnfermagemId == null || prescricaoEnfermagemId.getAtdSeq() == null || prescricaoEnfermagemId.getSeq() == null) {
			throw new IllegalArgumentException(
					"buscarCuidadosPrescricaoEnfermagem: parametros de filtro invalido");
		}
		
		PrescricaoEnfermagemVO prescricaoEnfermagemVO = new PrescricaoEnfermagemVO();
		
		// Busca dos Cuidados
		this.popularCuidadoEnfermagem(prescricaoEnfermagemVO, prescricaoEnfermagemId, listarTodas);
		
		return prescricaoEnfermagemVO.getListaCuidadoVO();
	}
	

	/**
	 * Popula um objeto PrescricaoEnfermagemVO com os 
	 * cuidados associados a uma Prescrição de Enfermagem.
	 * 
	 * @param prescricaoEnfermagemVO
	 * @param prescricaoEnfermagemId
	 * @param listarTodas
	 */
	private void popularCuidadoEnfermagem(PrescricaoEnfermagemVO prescricaoEnfermagemVO, 
			EpePrescricaoEnfermagemId prescricaoEnfermagemId, Boolean listarTodas) {
		EpePrescricoesCuidadosDAO epePrescricoesCuidadosDao = this.getEpePrescricoesCuidadosDAO();
		EpePrescricaoEnfermagem prescricaoEnfermagem = getEpePrescricaoEnfermagemDAO().obterPorChavePrimaria(prescricaoEnfermagemId);

		if(prescricaoEnfermagem != null && this.getEpePrescricaoEnfermagemDAO().contains(prescricaoEnfermagem)){
			getEpePrescricaoEnfermagemDAO().refresh(prescricaoEnfermagem);
		}
		
		List<EpePrescricoesCuidados> listaCuidadosEnfermagem = epePrescricoesCuidadosDao
				.pesquisarCuidadosPrescricao(prescricaoEnfermagem.getId(), prescricaoEnfermagem.getDthrFim(), listarTodas);
		for (EpePrescricoesCuidados prescricaoCuidado : listaCuidadosEnfermagem) {
			epePrescricoesCuidadosDao.refresh(prescricaoCuidado);
			CuidadoVO cuidadoVO = new CuidadoVO();
			cuidadoVO.setPrescricaoCuidado(prescricaoCuidado);
			cuidadoVO.setCuidado(prescricaoCuidado.getCuidado());
			cuidadoVO.setDescricao(prescricaoCuidado.getDescricaoFormatada());
			if (prescricaoCuidado.getCuidado().getIndRotina()) {
				cuidadoVO.setTipoPrescricaoCuidado(TipoPrescricaoCuidadoEnfermagem.CUIDADOS_MEDICOS_ROTINA);
			}
			else {
				cuidadoVO.setTipoPrescricaoCuidado(TipoPrescricaoCuidadoEnfermagem.CUIDADOS_MEDICOS);
			}
			prescricaoEnfermagemVO.adicionarCuidado(cuidadoVO);
		}
		
		if(listarTodas) {
			CollectionUtils.filter(listaCuidadosEnfermagem, new Predicate() {  
				public boolean evaluate(Object o) {  
					if(((EpePrescricoesCuidados)o).getPrescricaoCuidado() == null) {								
						return true;  
					}
					return false;  
				}  
			});
		}
		


		this.ordenarListaCuidadosPrescricaoEnfermagem(listaCuidadosEnfermagem);
	}	
	
	/**
	 * Orderna lista pela descricaoFormatada
	 * 
	 * @param list
	 */
	private void ordenarListaCuidadosPrescricaoEnfermagem(List<? extends EpePrescricoesCuidados> list) {
		Collections.sort(list, new Comparator<EpePrescricoesCuidados>() {
			@Override
			public int compare(EpePrescricoesCuidados item1, EpePrescricoesCuidados item2) {
				return item1.getDescricaoFormatada().compareTo(item2.getDescricaoFormatada());
			}
		});
	}
	
	/**
	 * Popula itens do relatório associados a uma Prescrição de Enfermagem. 
	 * 
	 * @param rescricaoEnfermagemVO
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<PrescricaoEnfermagemVO> popularRelatorioPrescricaoEnfermagem(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO) throws BaseException  {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.verificaListaCuidadosVO(prescricaoEnfermagemVO);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String aprazamento = StringUtils.EMPTY;	
		
		EpePrescricaoEnfermagem prescricaoEnfermagem = prescricaoEnfermagemVO.getPrescricaoEnfermagem();
		
		prescricaoEnfermagem = this.getEpePrescricaoEnfermagemDAO().merge(prescricaoEnfermagem);

		Integer numero = 1;
		for(CuidadoVO cuidadoVO : prescricaoEnfermagemVO.getListaCuidadoVO()) {
			EpePrescricoesCuidados cuidados = cuidadoVO.getPrescricaoCuidado();

			Short frequencia = null;
			if (cuidados.getFrequencia() != null) {
				frequencia = cuidados.getFrequencia().shortValue();
				cuidadoVO.setImprimeAprazamento(false);
			}
			
			aprazamento = gerarAprazamentoString(getPrescricaoEnfermagemFacade().gerarAprazamentoPrescricaoEnfermagem(
					prescricaoEnfermagem, cuidados.getDthrInicio(), cuidados
					.getDthrFim(), cuidados.getTipoFrequenciaAprazamento(),
					TipoItemAprazamento.CUIDADO, null, null, frequencia));

			if(aprazamento.isEmpty()){
				aprazamento = cuidadoVO.getDescricao().split(",")[cuidadoVO.getDescricao().split(",").length-1];
				if(aprazamento.substring(aprazamento.length()-1, aprazamento.length()).equals(";")){
					aprazamento = aprazamento.substring(0,aprazamento.length()-1);
				}
			}else if(aprazamento.equals("CAFE ALMOCO JANTA C A J ") || aprazamento.equals("CAFE ALMOCO JANTA C A J")){
				aprazamento = aprazamento.substring(0, 17);
			}
			cuidadoVO.setAprazamento(aprazamento);
			cuidadoVO.setDescricao(StringUtils.capitalize(cuidados.getDescricaoFormatada().toLowerCase()));
			cuidadoVO.setTipo("CUIDADOS");
			cuidadoVO.setNumero(numero++);
		}
		prescricaoEnfermagemVO.setOrdemTela(0);

		String validadePrescricaoEnfermagem = "Validade: de "
				+ sdf.format(prescricaoEnfermagemVO.getDthrInicio()) + " h. a "
				+ sdf.format(prescricaoEnfermagemVO.getDthrFim()) + " h.";

		prescricaoEnfermagemVO.setDataValidadePrescricao(validadePrescricaoEnfermagem);

		prescricaoEnfermagemVO.setSequencialPrescricaoEnfermagem(prescricaoEnfermagem.getId().getSeq());

		// Dados do médico que está confirmando
		StringBuilder medico = new StringBuilder();
		BuscaConselhoProfissionalServidorVO vo = null;
		
		vo = getPrescricaoMedicaFacade().buscaConselhoProfissionalServidorVO(
				servidorLogado.getId().getMatricula(), 
				servidorLogado.getId().getVinCodigo());

		String nomeMedico = vo.getNome();
		String siglaConselho = vo.getSiglaConselho();
		String numeroRegConselho = vo.getNumeroRegistroConselho();

		if (nomeMedico != null) {
			medico.append(nomeMedico + ___);
		}
		if (siglaConselho != null) {
			medico.append(siglaConselho + DOIS_PONTOS_);
		}
		if (numeroRegConselho != null) {
			medico.append(numeroRegConselho);
		}
		
		prescricaoEnfermagemVO.setMedicoConfirmacao(medico.toString());

		// Formata Prontuário
		if(prescricaoEnfermagemVO.getProntuario()!=null){
			prescricaoEnfermagemVO.setProntuarioFormatado(CoreUtil
						.formataProntuarioRelatorio(prescricaoEnfermagemVO.getProntuario()));
		}
		
		//Local
		String local = prescricaoEnfermagemVO.getLocal();
		local = local.replace("U:", "Unidade:");
		local = local.replace("Q:", "Quarto:");
		local = local.replace("L:", "Leito:");
		/*String local = "";
		AghAtendimentos atendimento = prescricaoEnfermagemVO.getPrescricaoEnfermagem().getAtendimento();
		if (atendimento.getLeito().getLeitoID() != null) {
			local = "Leito: " + atendimento.getLeito().getLeitoID();
		} else if (atendimento.getQuarto() != null) {
			local = "Quarto: " + atendimento.getQuarto().getDescricao();
		} else if (atendimento.getUnidadeFuncional() != null) {
			AghUnidadesFuncionais unidadeFuncional = atendimento
					.getUnidadeFuncional();
			local = "Unidade: " + unidadeFuncional.getAndarAlaDescricao();
		}*/
		prescricaoEnfermagemVO.setLocal(local);
			
		AghParametros parametroLimitePront = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_LIMIT_PRONT_VIRTUAL);
		
		// paciente recém nacido requer prontuário da mãe no relatório
		if (prescricaoEnfermagem.getAtendimento().getPaciente().getProntuario() != null
				&& prescricaoEnfermagem.getAtendimento().getPaciente().getProntuario() > parametroLimitePront.getVlrNumerico().longValue() 
				&& prescricaoEnfermagem.getAtendimento().getAtendimentoMae() != null) {

				prescricaoEnfermagemVO.setNomeMaePaciente(CoreUtil
						.formataProntuarioRelatorio(prescricaoEnfermagem.getAtendimento()
								.getPaciente().getProntuario())
						+ "          Mãe: "
						+ CoreUtil.formataProntuarioRelatorio(prescricaoEnfermagem
								.getAtendimento().getAtendimentoMae()
								.getPaciente().getProntuario()));
		}

		List<PrescricaoEnfermagemVO> relatorioPrescricaoEnfermagem = new ArrayList<PrescricaoEnfermagemVO>();
		relatorioPrescricaoEnfermagem.add(prescricaoEnfermagemVO);
		
		/*-------------VERIFICA SE DEVE IMPRIMIR NOVAS VIAS--------------- */
		Byte nroViasPen = this.obterNumeroDeViasRelatorioPrescricaoEnfermagem(prescricaoEnfermagem);
		if (nroViasPen > 1) {
			this.prepararImprimirNovasVias(prescricaoEnfermagemVO, relatorioPrescricaoEnfermagem, nroViasPen);
		}
		/*------------------------------------------- */

		return relatorioPrescricaoEnfermagem;
	}
	
	/**
	 * Prepara novas vias para serem impressas
	 * 
	 * @param voPai
	 * @param colVOPai
	 * @param internacao
	 */
	protected void prepararImprimirNovasVias(
			PrescricaoEnfermagemVO pEnfVO,
			List<PrescricaoEnfermagemVO> relatorioPrescricaoEnfermagem,
			Byte nroViasPen) {

		pEnfVO.setOrdemTela(1);
		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPen - 1; i++) {
			PrescricaoEnfermagemVO voPaiNovaVia = pEnfVO.copiar();
			voPaiNovaVia.setOrdemTela(ordemTela);
			relatorioPrescricaoEnfermagem.add(voPaiNovaVia);
			ordemTela++;
		}
	}
	
	private String gerarAprazamentoString(List<String> gerarAprazamento) {
		StringBuilder aprazamento = new StringBuilder("");

		if (gerarAprazamento != null) {
			for (String string : gerarAprazamento) {
				if(!string.contains("=")) {
					string = string.trim();
				}
				aprazamento.append(string);
				aprazamento.append(' ');
			}
		}
		return aprazamento.toString();
	}
	
	/**
	 * Obtém o número de vias de um relatório conforme a unidade funcional
	 * @param prescricaoEnfermagem
	 * @return
	 */
	public Byte obterNumeroDeViasRelatorioPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem){

		Byte nroViasPen = null;
		// Local de internação
		if (prescricaoEnfermagem.getAtendimento().getInternacao() != null) {
			AinInternacao internacao = prescricaoEnfermagem.getAtendimento()
					.getInternacao();
			if (internacao.getLeito() != null) {
				nroViasPen = internacao.getLeito().getUnidadeFuncional().getNroViasPen();
			} else if (internacao.getQuarto() != null) {
				nroViasPen = internacao.getQuarto().getUnidadeFuncional().getNroViasPen();
			} else if (internacao.getUnidadesFuncionais() != null) {
				nroViasPen = internacao.getUnidadesFuncionais().getNroViasPen();
			}
		}
		if (nroViasPen == null){
			nroViasPen = 1;
		}
		
		return nroViasPen;
	}
	
	public void verificaListaCuidadosVO(PrescricaoEnfermagemVO prescricaoEnfermagemVO) throws BaseException  {
		
		if (prescricaoEnfermagemVO.getListaCuidadoVO() == null || prescricaoEnfermagemVO.getListaCuidadoVO().isEmpty()) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoEnfermagemONExceptionCode.NENHUM_CUIDADO_PARA_IMPRIMIR);
		}
	}
	
	protected IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
