package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemReceitaMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ReceitaMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ManterReceituarioON extends BaseBusiness {

	private static final String MSG_PARAMETRO_OBRIGATORIO = "Parâmetro obrigatório";

	private static final Log LOG = LogFactory.getLog(ManterReceituarioON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MamReceituariosDAO mamReceituariosDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 7573446492435352835L;

	/**
	 * Retorna os objetos contendo os dados das receitas médicas do sumário de
	 * alta fornecido.
	 * 
	 * @param altaSumario
	 * @param atualizar
	 *            true para concluir as receitas e atualizar indicador de
	 *            impressão
	 * @throws BaseException 
	 */
	public List<ReceitaMedicaVO> imprimirReceita(MpmAltaSumario altaSumario,
			boolean atualizar) throws BaseException {

		if (altaSumario == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_OBRIGATORIO);
		}

		altaSumario = this.getPrescricaoMedicaFacade().obterAltaSumario(
				altaSumario.getId());

		List<ReceitaMedicaVO> listaReceitaMedicaVO = new ArrayList<ReceitaMedicaVO>();

		// Percorre as receitas pertencentes a alta de sumário
		List<MamReceituarios> receitas = this.getMamReceituariosDAO()
				.buscarReceituariosPorAltaSumario(altaSumario);
		for (MamReceituarios receita : receitas) {

			// Percorrer os grupos de impressão e as validades de meses
			// configurados para a receita
			List<Object[]> configuracaoImpressaoItensReceituario = this
					.getAmbulatorioFacade()
					.buscarConfiguracaoImpressaoItensReceituario(receita);
			for (Object[] itemReceituario : configuracaoImpressaoItensReceituario) {

				Byte grupoImpressao = (Byte) itemReceituario[0];
				Byte validadeMeses = (Byte) itemReceituario[1];

				int meses = (validadeMeses == null) ? 1 : validadeMeses;
				for (int mes = 1; mes <= meses; mes++) {

					int numeroVias = (receita.getNroVias() == null) ? 1
							: (receita.getNroVias() % 2) != 0 ? receita
									.getNroVias() + 1 : receita.getNroVias();
					for (int via = 1; via <= numeroVias; via++) {

						Integer inicio = 0;

						ReceitaMedicaVO vo = this.carregarDadosRelatorio(
								receita, grupoImpressao, validadeMeses,
								(byte) via, mes, inicio);
						listaReceitaMedicaVO.add(vo);
						inicio = inicio + 2;
					}
				}
			}

			if (atualizar) {
				this.atualizarReceituario(receita);
			}
		}
		
		return listaReceitaMedicaVO;
	}

	/**
	 * Realizar as atualizações necessárias na receita.<br>
	 * 1º Concluir a receita;<br>
	 * 2º Concluir a receita do auto-relacionamento quando existir;<br>
	 * 3º Atualizar o indicador de impressão para 'S';
	 * 
	 * @param receituario
	 * @throws ApplicationBusinessException  
	 */
	private void atualizarReceituario(MamReceituarios receituario) throws ApplicationBusinessException {
		this.concluirReceituariosPendentes(receituario);
		this.atualizarIndicadorImpressao(receituario);
		this.getMamReceituariosDAO().merge(receituario);
		this.getMamReceituariosDAO().flush();
	}

	/**
	 * Atualizar o indicador de impressão
	 * 
	 * @param receituario
	 */
	private void atualizarIndicadorImpressao(MamReceituarios receituario) {
		if (receituario == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_OBRIGATORIO);
		}
		receituario.setIndImpresso(DominioSimNao.S);
	}

	/**
	 * Concluir os receituários e o auto-relacionamento quando existir
	 * 
	 * @param receituario
	 * @throws ApplicationBusinessException  
	 */
	private void concluirReceituariosPendentes(MamReceituarios receituario) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (receituario == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_OBRIGATORIO);
		}

		if (DominioIndPendenteAmbulatorio.P
				.equals(receituario.getPendente())) {
			return;
		}

		receituario.setPendente(DominioIndPendenteAmbulatorio.V);
		receituario.setDthrValida(Calendar.getInstance().getTime());
		receituario.setServidorValida(servidorLogado);

		if (receituario.getReceituario() != null) {
			MamReceituarios receituarioMvto = receituario.getReceituario();
			receituarioMvto.setPendente(DominioIndPendenteAmbulatorio.V);
			receituarioMvto.setDthrValidaMvto(Calendar.getInstance().getTime());
			receituarioMvto.setServidorValidaMovimento(servidorLogado);

			this.getMamReceituariosDAO().merge(receituarioMvto);
		}
	}

	/**
	 * Carregar os dados que serão impressos no relatório.
	 * 
	 * @param receituario
	 * @param grupoImpressao
	 * @param validadeMeses
	 * @param via
	 * @param mesDeUso
	 * @param inicio
	 * @return
	 * @throws BaseException 
	 */
	private ReceitaMedicaVO carregarDadosRelatorio(MamReceituarios receituario,
			Byte grupoImpressao, Byte validadeMeses, Byte via,
			Integer mesDeUso, Integer inicio) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		ReceitaMedicaVO receitaMedicaVO = new ReceitaMedicaVO(receituario
				.getPaciente().getNome());
		receitaMedicaVO.setReceituarioSeq(receituario.getSeq());
		receitaMedicaVO.setTipo(receituario.getTipo().getDescricao());
		receitaMedicaVO.setVia(via);
		receitaMedicaVO.setMesDeUso(mesDeUso);
		receitaMedicaVO.setInicio(inicio);
		receitaMedicaVO.setValidadeMeses(validadeMeses);
		
		// @ORADB MAMC_GET_END_PAC(PAC.CODIGO) ENDERECO
		Integer pacCodigo;
		if(receituario.getMpmAltaSumario()!=null){
			pacCodigo = receituario.getMpmAltaSumario().getPaciente().getCodigo();
		}
		else{
			pacCodigo = receituario.getPaciente().getCodigo();
		}
		VAipEnderecoPaciente enderecoPaciente = getCadastroPacienteFacade()
				.obterEndecoPaciente(pacCodigo);
		if (enderecoPaciente != null) {
			receitaMedicaVO.setEndereco(enderecoPaciente
					.getStringEnderecoCompleto());
		}
		// adiciona um mes na data para cada mes em que a receita é valida
		Calendar aPartirDe = Calendar.getInstance();
		aPartirDe.add(Calendar.MONTH, (mesDeUso.intValue() - 1));
		receitaMedicaVO.setPartirDe(aPartirDe.getTime());

		List<ItemReceitaMedicaVO> itens = getAmbulatorioFacade()
				.obterItemReceituarioPorImpressaoValidade(receituario,
						grupoImpressao, validadeMeses);
		receitaMedicaVO.setItens(itens);

		// busca dados de registro no conselho profissional
		BuscaConselhoProfissionalServidorVO conselho = this
				.getPrescricaoMedicaFacade()
				.buscaConselhoProfissionalServidorVO(
						receituario.getServidorValida());

		//se usuario logado for médico carrega dados do medico senao carrega apenas o nome do medico com o nome da pessoa do servidor logado 
		if (conselho != null && conselho.getNome()!=null) {
//			receitaMedicaVO.setNomeMedico(conselho.getNome());
			receitaMedicaVO.setNomeMedico(capitalizeString(conselho.getNome()));
			receitaMedicaVO.setSiglaConselho(conselho.getSiglaConselho());
			receitaMedicaVO.setRegistroConselho(conselho
					.getNumeroRegistroConselho());
			receitaMedicaVO.setEspecialidade(obterEspecialidade(receituario.getServidor().getId().getMatricula(), receituario.getServidor().getId().getVinCodigo()));

		}else{
			receitaMedicaVO.setNomeMedico(capitalizeString(servidorLogado.getPessoaFisica().getNome()));
			receitaMedicaVO.setEspecialidade(obterEspecialidade(receituario.getServidor().getId().getMatricula(), receituario.getServidor().getId().getVinCodigo()));
		}
		
		AghParametros ufSede = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
		AghParametros cidadeSede = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
		
//		AghParametros logradouro = getAghuFacade().buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO);
		
		if (ufSede != null && ufSede.getVlrTexto() != null) {
			receitaMedicaVO.setUfConselho(ufSede.getVlrTexto());
			receitaMedicaVO.setUfHU(ufSede.getVlrTexto());
		} else {
			receitaMedicaVO.setUfConselho("");
			receitaMedicaVO.setUfHU("");
		}
		
		if (cidadeSede != null && cidadeSede.getVlrTexto() != null) {
			receitaMedicaVO.setCidadeHU(cidadeSede.getVlrTexto());
		} else {
			receitaMedicaVO.setCidadeHU("");
		}

		return receitaMedicaVO;
	}
	
	/**
	 * Retorna a receita médica pelo seq.
	 * 
	 * @param receitaSeq
	 * @param imprimiu
	 *            true para concluir as receitas e atualizar indicador de
	 *            impressão
	 * @throws BaseException 
	 */
	public List<ReceitaMedicaVO> imprimirReceita(Long receitaSeq, Boolean imprimiu) throws BaseException {

		if (receitaSeq == null) {
			throw new IllegalArgumentException(MSG_PARAMETRO_OBRIGATORIO);
		}

		List<ReceitaMedicaVO> listaReceitaMedicaVO = new ArrayList<ReceitaMedicaVO>();

		// Percorre as receitas pertencentes a alta de sumário
		MamReceituarios receita = this.getMamReceituariosDAO().obterPorChavePrimaria(receitaSeq);

		// Percorrer os grupos de impressão e as validades de meses
		// configurados para a receita
		List<Object[]> configuracaoImpressaoItensReceituario = this
				.getAmbulatorioFacade()
				.buscarConfiguracaoImpressaoItensReceituario(receita);
		for (Object[] itemReceituario : configuracaoImpressaoItensReceituario) {

			Byte grupoImpressao = (Byte) itemReceituario[0];
			Byte validadeMeses = (Byte) itemReceituario[1];

			int meses = (validadeMeses == null) ? 1 : validadeMeses;
			for (int mes = 1; mes <= meses; mes++) {

				int numeroVias = (receita.getNroVias() == null) ? 1
						: (receita.getNroVias() % 2) != 0 ? receita
								.getNroVias() + 1 : receita.getNroVias();
				for (int via = 1; via <= numeroVias; via++) {

					Integer inicio = 0;

					ReceitaMedicaVO vo = this.carregarDadosRelatorio(
							receita, grupoImpressao, validadeMeses,
							(byte) via, mes, inicio);
					
					vo.setEspecialidade(this.obterEspecialidade(
							receita.getServidor().getId().getMatricula(),
							receita.getServidor().getId().getVinCodigo()));
										
					listaReceitaMedicaVO.add(vo);
					inicio = inicio + 2;
				}
			}
		}

		if (!imprimiu) {
			this.atualizarIndicadorImpressao(receita);
		}
		
		return listaReceitaMedicaVO;
	}

	
	
	/**
	 * ORADB FUNCTION MAMC_GET_ESPEC_MED
	 * 
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	public String obterEspecialidade(Integer matricula, Short vinCodigo) {
		StringBuilder desc = new StringBuilder();
		String descricao = "";
		String descricaoCct = "";
		
		final AghEspecialidades especialidade = this.getAghuFacade()
			.obterEspecialidadePorServidor(matricula, vinCodigo);
		
		if(especialidade != null) {
			descricao = this.getAmbulatorioFacade().obterDescricaoCidCapitalizada(especialidade.getNomeEspecialidade());
			if(especialidade.getCentroCusto() != null) {
				descricaoCct = this.getAmbulatorioFacade().obterDescricaoCidCapitalizada(
						especialidade.getCentroCusto().getDescricao());
			}
			if(DominioSimNao.S == especialidade.getIndImpSoServico()) {
				desc.append(descricaoCct).append(" - ").append(descricao);
			} else {
				desc.append(descricaoCct);
			}
		}
				
		return desc.toString();
	}
	/**
	 * #43087
	 * Incluida C4 obter especialidade utilizando numero da consulta
	 * @return
	 */
	public String obterEspecialidade(Integer conNumero){
		StringBuilder desc = new StringBuilder();
		AghEspecialidades especialidade = this.getAghEspecialidadesDAO().obterEspecialidadePorNumeroConsulta(conNumero);
		if(especialidade != null) {
			if(especialidade.getCentroCusto()!=null){
				desc.append(especialidade.getCentroCusto().getDescricao());
				desc.append(" - ");
			}
			desc.append(especialidade.getNomeEspecialidade());
		}
		return desc.toString();
	}
	
	
	public static String capitalizeString(String string) {
		
		if (string != null){
			char[] chars = string.toLowerCase().toCharArray();
			boolean found = false;
			for (int i = 0; i < chars.length; i++) {
				if (!found && Character.isLetter(chars[i])) {
					chars[i] = Character.toUpperCase(chars[i]);
					found = true;
				} else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
					found = false;
				}
			}
			return String.valueOf(chars); 
		}
		
		return "";
	}
	

	/**
	 * ORADB FUNCTION MAMC_GET_ESPEC_MED
	 * 
	 * @param esp_seq
	 * @return
	 */
	
	public String obterEspecialidade(Short espSeq) {

		Integer vCctCodigo = null;
		DominioSimNao vIndServico = null;
		String vDescricao = "", vDescricaoCct = "", vDesc = "";

		AghEspecialidadeVO especialidade = this.aghEspecialidadesDAO.obterEspecialidadePorEspSeq(espSeq);

		if (especialidade != null) {

			if (especialidade.getCctCodigo() != null) {
				vCctCodigo = especialidade.getCctCodigo();
			}
			if (especialidade.getIndImpSoServico() != null) {
				vIndServico = especialidade.getIndImpSoServico();
			}
			if (especialidade.getEspNomeEspecialidade() != null) {
				vDescricao = this.getAmbulatorioFacade().obterDescricaoCidCapitalizada(especialidade.getEspNomeEspecialidade());
			}
		}
		FccCentroCustos centroCusto = this.fccCentroCustosDAO.obterDescricaoCentroCustoPorCodigo(vCctCodigo);
		if(centroCusto != null){
			vDescricaoCct = this.getAmbulatorioFacade().obterDescricaoCidCapitalizada(centroCusto.getDescricao());
		}

		if (vIndServico != null && vIndServico == DominioSimNao.S) {
			vDesc = vDescricaoCct + " - " + vDescricao;
		} else {
			vDesc = vDescricaoCct;
		}
		return vDesc;
	}
	/** GET/SET **/
	private MamReceituariosDAO getMamReceituariosDAO() {
		return mamReceituariosDAO;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public AghEspecialidadesDAO getAghEspecialidadesDAO() {
		return aghEspecialidadesDAO;
	}

	public void setAghEspecialidadesDAO(AghEspecialidadesDAO aghEspecialidadesDAO) {
		this.aghEspecialidadesDAO = aghEspecialidadesDAO;
	}
	
}
