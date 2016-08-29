package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.AmostraVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAndamentoVO;
import br.gov.mec.aghu.exames.vo.AelAmostraRecebidaVO;
import br.gov.mec.aghu.exames.vo.AelAmostrasVO;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@Stateless
public class AgruparExamesON extends BaseBusiness {
	
	private static final String _HIFEN_ = " - ";

	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;
	
	@EJB
	private AgruparExamesRN agruparExamesRN;
	
	private static final Log LOG = LogFactory.getLog(AgruparExamesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5220148615023412795L;

	public enum AgruparExamesONMessageCode implements BusinessExceptionCode {
		CONTRATADO_PROFESSOR_EXISTENTE, RESIDENTE_EXISTENTE, PATOLOGISTA_ASSOCIADO_SUCESSO, ITENS_NAO_SELECIONADOS, 
		GRUPO_AMOSTRA_EXAME_TIPO_ERRO, GRUPO_AMOSTRA_EXAME_SUCESSO,
		AMOSTRAS_AGRUPADAS_ERRO, AMOSTRAS_AGRUPADAS_SUCESSO, AMOSTRAS_DESAGRUPADAS_SUCESSO, PATOLOGISTA_NAO_INFORMADO;
	}
	
	/**
	 * Retorna codigos de amostras caso tenho sido selecionada todas amostras ou apenas uma
	 * 
	 * @param listaAmostrasRecebidas lista de amostras enviadas para recebimento
	 * @param amostraRecebida amostra selecionada para recebimento
	 * @return
	 */
	public List<Integer> getSeqpAmostrasRecebidas(List<AelAmostrasVO> listaAmostrasRecebidas) {
		final List<Integer> resultado = new ArrayList<Integer>();
		if(listaAmostrasRecebidas != null){
			for (AelAmostrasVO amostrasVo : listaAmostrasRecebidas) {
				resultado.add(amostrasVo.getSeqp().intValue());
			}
		}
		return resultado;
	}
	
	
	/**
	 * Seleciona um exame na grid como selecionado
	 * 
	 * @param exameAndamento exame selecionado na grid
	 */
	public void selecionarExameAndamento(ExameAndamentoVO exameAndamento, List<ExameAndamentoVO> listaExamesAndamento, Set<ExameAndamentoVO> listaExamesSelecionados) {
		for (ExameAndamentoVO vo : listaExamesAndamento) {
			if(vo.equals(exameAndamento)) {
				vo.setSelecionado(exameAndamento.isSelecionado());
				if(exameAndamento.isSelecionado()) {
					listaExamesSelecionados.removeAll(listaExamesSelecionados);
					listaExamesSelecionados.add(exameAndamento);
				} else {
					listaExamesSelecionados.removeAll(listaExamesSelecionados);
				}
			} else {
				vo.setSelecionado(false);
			}
		}
	}

	/**
	 * Seleciona uma amostra na grid de amostras
	 * 
	 * @param amostraVO amostra selecionada
	 */
	public boolean selecionarAmostraExibirPatologistaResponsavel(AmostraVO amostraVO, List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas) {
		boolean exibirPanelPatologistaResponsavel = false;
		
		for (AmostraVO vo : listaAmostras) {
			if(vo.equals(amostraVO)) {
				vo.setSelecionado(amostraVO.isSelecionado());
				if(amostraVO.isSelecionado()) {
					exibirPanelPatologistaResponsavel = Boolean.TRUE;
					listaAmostraSelecionadas.add(amostraVO);
				} else {
					listaAmostraSelecionadas.remove(amostraVO);
					if(listaAmostraSelecionadas.size() == 0) {
						exibirPanelPatologistaResponsavel = Boolean.FALSE;
					} else {
						exibirPanelPatologistaResponsavel = Boolean.TRUE;
					}
				}
				break;
			}
		}
		return exibirPanelPatologistaResponsavel;
	}
	
	/**
	 * Desmarca a selecao de todas amostras
	 * 
	 * @param listaAmostras lista de amostras
	 * @param listaAmostraSelecionadas lista de amostras selecionadas
	 */
	public void desfazerSelecaoTodasAmostras(List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas) {
		listaAmostraSelecionadas.clear();
		if(listaAmostras != null && !listaAmostras.isEmpty()) {
			for (AmostraVO vo : listaAmostras) {
				vo.setSelecionado(Boolean.FALSE);
			}
		}
	}
	
	
	/**
	 * Seleciona todas amostras da grid de amostras marcando elemento da lista como selecionado
	 * 
	 * @param todasAmostrasSelecionadas indica se todas amostras foram selecionadas
	 * @param listaAmostras lista de amostras
	 * @param listaAmostraSelecionadas lista de amostras selecionadas
	 */
	public void selecionarTodasAmostras(boolean todasAmostrasSelecionadas, List<AmostraVO> listaAmostras, List<AmostraVO> listaAmostraSelecionadas) {
		if(todasAmostrasSelecionadas) {
			for (AmostraVO vo : listaAmostras) {
				vo.setSelecionado(Boolean.TRUE);
			}
			listaAmostraSelecionadas.clear();
			listaAmostraSelecionadas.addAll(listaAmostras);
		} else {
			desfazerSelecaoTodasAmostras(listaAmostras, listaAmostraSelecionadas);
		}
	}
	
	
	//22049 - RN02
	/**
	 * Adiciona um patologista na lista de patologistas vindo da suggestionbox
	 * 
	 * @param listaPatologistasResponsaveis lista que contem patologistas informados
	 * @param novoPatologistaResponsavel patologista encontrado na suggestionbox
	 */
	public void addicionarPatologistaResponsavel(List<AelPatologista> listaPatologistasResponsaveis, AelPatologista novoPatologistaResponsavel)
			throws ApplicationBusinessException {
		this.validaPatologistasResponsaveisParaAgrupamento(listaPatologistasResponsaveis, novoPatologistaResponsavel);
		listaPatologistasResponsaveis.add(novoPatologistaResponsavel);
	}
	
	//22049 - RN02
	private void validaPatologistasResponsaveisParaAgrupamento(List<AelPatologista> listaPatologistasResponsaveis, AelPatologista novoPatologistaResponsavel)
			throws ApplicationBusinessException {
		if (novoPatologistaResponsavel != null && novoPatologistaResponsavel.getSeq() != null && listaPatologistasResponsaveis != null
				&& !listaPatologistasResponsaveis.isEmpty()) {
			for (AelPatologista patologistaAnterior : listaPatologistasResponsaveis) {
				if ((patologistaAnterior.getFuncao().equals(DominioFuncaoPatologista.C) && novoPatologistaResponsavel.getFuncao().equals(
						DominioFuncaoPatologista.C))
						|| (patologistaAnterior.getFuncao().equals(DominioFuncaoPatologista.P) && novoPatologistaResponsavel.getFuncao().equals(
								DominioFuncaoPatologista.P))) {
					throw new ApplicationBusinessException(AgruparExamesONMessageCode.CONTRATADO_PROFESSOR_EXISTENTE);
				} else if (patologistaAnterior.getFuncao().equals(DominioFuncaoPatologista.R)
						&& novoPatologistaResponsavel.getFuncao().equals(DominioFuncaoPatologista.R)) {
					throw new ApplicationBusinessException(AgruparExamesONMessageCode.RESIDENTE_EXISTENTE);
				} else if ((patologistaAnterior.getFuncao().equals(DominioFuncaoPatologista.P) && novoPatologistaResponsavel.getFuncao().equals(
						DominioFuncaoPatologista.C))
						|| (patologistaAnterior.getFuncao().equals(DominioFuncaoPatologista.C) && novoPatologistaResponsavel.getFuncao().equals(
								DominioFuncaoPatologista.P))) {
					throw new ApplicationBusinessException(AgruparExamesONMessageCode.CONTRATADO_PROFESSOR_EXISTENTE);
				}
			}
		}
	}
	
	//#22049- RN03
	/**
	 * Obtem as amostras de um exame dado um numero de solicitacao de exame
	 * 
	 * @param solicitacaoNumero numero de solicitacao do exame
	 */
	public List<AmostraVO> obterAmostrasSolicitacao(Integer solicitacaoNumero, List<Integer> numerosAmostras, final Map<AghuParametrosEnum, String> situacao) {
		List<AmostraVO> amostrasSolicitacao = getAgruparExameRN().obterAmostrasSolicitacao(solicitacaoNumero, numerosAmostras, situacao);
		
		for (AmostraVO amostraVO : amostrasSolicitacao) {
			amostraVO.setMaterial(formatarMaterial(amostraVO));
		}
		
		return amostrasSolicitacao;
		
	}
	
	/**
	 * Adiciona um ou mais patologistas responsaveis para uma ou mais amostras
	 * ou grupo de amostras
	 * 
	 * @param listaAmostras lista de amostras dado um numero de solicitacao de exame
	 * @param listaPatologistasResponsaveis lista de patologistas que foram adicionados pela suggestionbox
	 */
	public void confirmarPatologistasResponsaveis(List<AmostraVO> listaAmostras, List<AelPatologista> listaPatologistasResponsaveis) {

		for (AmostraVO amostraVO:  listaAmostras) {
			if(amostraVO.isSelecionado()) {
				amostraVO.setPatologistasResponsaveis(listaPatologistasResponsaveis);
				StringBuilder patologista = new StringBuilder();
				
				for (AelPatologista aelPatologista : listaPatologistasResponsaveis) {
					if(patologista.length() > 0) {
						patologista.append('\n');
					}
					patologista.append(aelPatologista.getNome());
					patologista.append(_HIFEN_);
					patologista.append(aelPatologista.getFuncao().getDescricao());
				}
				
				amostraVO.setPatologista(patologista.toString());

			}
		}
	}

	/**
	 * Agrupa amostras que possuam mesmo tipo de exame
	 * 
	 * @param listaAmostraSelecionadas
	 *            lista de amostras selecionadas na grid de amostras
	 * @param listaExamesSelecionados
	 *            lista de exames selecionados na grid de exames
	 * @param listaAmostras
	 *            lista de amostras dado um numero de solicitacao de exame
	 * @throws ApplicationBusinessException
	 */
	public void agruparAmostras(List<AmostraVO> listaAmostraSelecionadas, Set<ExameAndamentoVO> listaExamesSelecionados, List<AmostraVO> listaAmostras,
			List<AelPatologista> listaPatologistasResponsaveis)
			throws ApplicationBusinessException {

		if(validarItensSelecionadosParaAgrupamento(listaAmostraSelecionadas, listaExamesSelecionados)) {
			AgruparExamesONMessageCode messageCode = validarTiposExames(listaAmostraSelecionadas, listaExamesSelecionados);
			if(messageCode == AgruparExamesONMessageCode.AMOSTRAS_AGRUPADAS_SUCESSO || messageCode == AgruparExamesONMessageCode.GRUPO_AMOSTRA_EXAME_SUCESSO) {
				ExameAndamentoVO andamentoVO = null;
				if (!listaExamesSelecionados.isEmpty()) {
					andamentoVO = listaExamesSelecionados.iterator().next();
				}

				AmostraVO amostraAgrupada = new AmostraVO();
				List<AmostraVO> amostrasAgrupadas = new ArrayList<AmostraVO>();
				StringBuilder materialAmostraAgrupada = new StringBuilder();
				for (AmostraVO amostraVO : listaAmostraSelecionadas) {
					amostraVO.setSelecionado(false);
					amostraAgrupada.setTipoExame(amostraVO.getTipoExame());
					if (amostraVO.getAmostrasAgrupadas() == null || amostraVO.getAmostrasAgrupadas().isEmpty()) {
						amostrasAgrupadas.add(amostraVO);
					} else {
						amostrasAgrupadas.addAll(amostraVO.getAmostrasAgrupadas());
					}
					materialAmostraAgrupada.append(amostraVO.getMaterial());
					materialAmostraAgrupada.append('\n');
					listaAmostras.remove(amostraVO);
				}
				
				//@TODO: verificar uma melhor solucao para remover \nlistaAmostraSelecionadas
				materialAmostraAgrupada.replace(materialAmostraAgrupada.length()-1, materialAmostraAgrupada.length(), "");
				
				amostraAgrupada.setAmostrasAgrupadas(amostrasAgrupadas);
				amostraAgrupada.setMaterial(materialAmostraAgrupada.toString());
				amostraAgrupada.setExameAndamentoVO(andamentoVO);
				
				
				if (andamentoVO == null) {
					amostraAgrupada.setPatologistasResponsaveis(new ArrayList<AelPatologista>(listaPatologistasResponsaveis));
				} else {
					if (listaPatologistasResponsaveis == null) {
						listaPatologistasResponsaveis = new ArrayList<AelPatologista>();
					} else {
						listaPatologistasResponsaveis.clear();
					}
					amostraAgrupada.setTipoExame(andamentoVO.getTipoExame());
					amostraAgrupada.setNumeroExame(andamentoVO.getNumeroExame());
					if (andamentoVO.getPatologistaResponsavel() != null) {
						StringBuilder patologistaResponsavel = new StringBuilder();
						patologistaResponsavel.append(andamentoVO.getPatologistaResponsavel().getNome())
						.append(_HIFEN_)
						.append(andamentoVO.getPatologistaResponsavel().getFuncao().getDescricao());
						amostraAgrupada.setPatologista(patologistaResponsavel.toString());
					}
					amostraAgrupada.setPatologistasResponsaveis(new ArrayList<AelPatologista>());
					for (final AelPatologista aelPatologista : andamentoVO.getAelPatologistas()) {
						amostraAgrupada.getPatologistasResponsaveis().add(aelPatologista);
						//listaPatologistasResponsaveis.add(aelPatologista);
					}

				}
				amostraAgrupada.setSelecionado(true);
				amostraAgrupada.setAgrupada(true);
				
				//remove todos selecionados e coloca apenas o agrupado
				listaAmostraSelecionadas.removeAll(listaAmostraSelecionadas);
				listaAmostras.add(amostraAgrupada);
				listaAmostraSelecionadas.add(amostraAgrupada);
				

				//				StatusMessages.instance().addFromResourceBundle(Severity.INFO, messageCode.toString());
			} else {
				throw new ApplicationBusinessException(messageCode, Severity.ERROR);
				//				StatusMessages.instance().addFromResourceBundle(Severity.ERROR, messageCode.toString());
			}
		} else {
			throw new ApplicationBusinessException(AgruparExamesONMessageCode.ITENS_NAO_SELECIONADOS, Severity.ERROR);
			//			StatusMessages.instance().addFromResourceBundle(Severity.ERROR, AgruparExamesONMessageCode.ITENS_NAO_SELECIONADOS.toString());
		}
	}
	
	/**
	 * Desagrupa amostras agrupadas e devolve as mesmas para lista de amostras
	 * 
	 * @param listaAmostraSelecionadas lista de amostras selecionadas na grid de amostras
	 * @param listaAmostras lista de amostras dado um numero de solicitacao de exame
	 * @param amostraVO amostra agrupada que sera desagrupada
	 */
	public void desagruparAmostras(List<AmostraVO> listaAmostraSelecionadas, List<AmostraVO> listaAmostras, AmostraVO amostraVO) {
		listaAmostras.remove(amostraVO);
		listaAmostraSelecionadas.remove(amostraVO);
		// adiciona as amostras associadas novamente na lista para associação.
		List<AmostraVO> amostras = amostraVO.getAmostrasAgrupadas();
		for (final AmostraVO amostra : amostras) {
			amostra.setPatologistasResponsaveis(new ArrayList<AelPatologista>(amostraVO.getPatologistasResponsaveis()));
			amostra.setExameAndamentoVO(null);
		}
		listaAmostras.addAll(amostras);
	}
	
	//QUALIDADE
	/**
	 * Grava amostras entre si ou com outros exames conforme RN06 da estoria
	 * #22049
	 * 
	 * @param listaAmostras
	 *            lista de amostras selecionadas na grid de amostras
	 * @param solicitacaoNumero
	 *            numero de solicitacao de um exame
	 * @throws MECBaseException
	 */
	public List<AelAmostraRecebidaVO> gravarAmostras(final List<AmostraVO> listaAmostras, final Integer solicitacaoNumero, final RapServidores servidorLogado,
			final AghUnidadesFuncionais unidadeExecutora, final List<ExameAndamentoVO> listaExamesAndamento,
			final String nomeMicrocomputador, final Map<AghuParametrosEnum, String> situacao) throws BaseException {
		if (listaAmostras != null && !listaAmostras.isEmpty()) {
			this.validarPatologistasResponsaveis(listaAmostras);
			this.validarTiposExames(listaAmostras);

			return this.getAgruparExameRN().gravarAmostras(listaAmostras, solicitacaoNumero, servidorLogado, unidadeExecutora, 
					listaExamesAndamento, nomeMicrocomputador, situacao);
		}
		return new ArrayList<AelAmostraRecebidaVO>(0);
	}

	private void validarTiposExames(List<AmostraVO> listaAmostras) throws ApplicationBusinessException {
		for (AmostraVO amostraVO : listaAmostras) {
			if (amostraVO.getExameAndamentoVO() != null) {
				if (!amostraVO.getExameAndamentoVO().getTipoExame().getSeq().equals(amostraVO.getTipoExame().getSeq())) {
					throw new ApplicationBusinessException(AgruparExamesONMessageCode.AMOSTRAS_AGRUPADAS_ERRO, Severity.ERROR);
				}
			}
			if (amostraVO.getAmostrasAgrupadas() != null && !amostraVO.getAmostrasAgrupadas().isEmpty()) {
				this.validarTipoExame(amostraVO.getAmostrasAgrupadas(), amostraVO.getTipoExame());
			}
		}
	}

	private void validarTipoExame(final List<AmostraVO> listaAmostras, final AelConfigExLaudoUnico tipoExame) throws ApplicationBusinessException {
		for (AmostraVO amostra : listaAmostras) {
			if (!amostra.getTipoExame().getSeq().equals(tipoExame.getSeq())) {
				throw new ApplicationBusinessException(AgruparExamesONMessageCode.AMOSTRAS_AGRUPADAS_ERRO, Severity.ERROR);
			}
		}
	}

	//QUALIDADE
	private void validarPatologistasResponsaveis(List<AmostraVO> listaAmostrasSelecionadas) throws ApplicationBusinessException {
		for (AmostraVO amostraVO : listaAmostrasSelecionadas) {
			if (amostraVO.getPatologistasResponsaveis() == null || amostraVO.getPatologistasResponsaveis().isEmpty()) {
				throw new ApplicationBusinessException(AgruparExamesONMessageCode.PATOLOGISTA_NAO_INFORMADO, Severity.ERROR);
			}
			boolean patResponsavel = false;
			for (final AelPatologista patologista : amostraVO.getPatologistasResponsaveis()) {
				if (DominioFuncaoPatologista.P.equals(patologista.getFuncao()) || DominioFuncaoPatologista.C.equals(patologista.getFuncao())) {
					patResponsavel = true;
					break;
				}
			}
			if (!patResponsavel) {
				throw new ApplicationBusinessException(AgruparExamesONMessageCode.PATOLOGISTA_NAO_INFORMADO, Severity.ERROR);
			}
		}
	}
	
	private boolean validarItensSelecionadosParaAgrupamento(List<AmostraVO> listaAmostraSelecionadas, Set<ExameAndamentoVO> listaExamesSelecionados) {
		final boolean exameSelecionado = !listaExamesSelecionados.isEmpty();
		final boolean amostraSelecionada = !listaAmostraSelecionadas.isEmpty();
		int totalAmostrasSelecionadas = 0;
		boolean agrupamentoValido = false;
		
		if (amostraSelecionada) {
			totalAmostrasSelecionadas = listaAmostraSelecionadas.size();
		}
		
		if(exameSelecionado && amostraSelecionada) {
			agrupamentoValido = true;
		} else if (!exameSelecionado && amostraSelecionada && totalAmostrasSelecionadas > 1) { //pelos menos uma amostra precisa ser selecionada
			agrupamentoValido = true;
		}
		
		return agrupamentoValido;
	}
	
	private AgruparExamesONMessageCode validarTiposExames(List<AmostraVO> listaAmostraSelecionadas, Set<ExameAndamentoVO> listaExamesSelecionados) {
		boolean agrupamentoExameAmostraValido = true;
		AgruparExamesONMessageCode messageCodeRetorno = null;
		
		if(listaExamesSelecionados.size() > 0) { //agrupamento de exame com amostra
			ExameAndamentoVO exameAndamento = listaExamesSelecionados.iterator().next();
			
			for (AmostraVO amostraVO : listaAmostraSelecionadas) {
				if(!amostraVO.getTipoExame().getSigla().equals(exameAndamento.getTipoExame().getSigla())) {
					agrupamentoExameAmostraValido = false;					
					break;
				}
			}
			
			if(agrupamentoExameAmostraValido) {
				messageCodeRetorno = AgruparExamesONMessageCode.GRUPO_AMOSTRA_EXAME_SUCESSO;
			} else {
				messageCodeRetorno = AgruparExamesONMessageCode.GRUPO_AMOSTRA_EXAME_TIPO_ERRO;
			}
		} else {//agrupamento apenas entre amostras
			Map<String, String> tiposExamesAmostras = new HashMap<String, String>();
			
			for (AmostraVO amostraVO : listaAmostraSelecionadas) {
				tiposExamesAmostras.put(amostraVO.getTipoExame().getSigla(), amostraVO.getTipoExame().getSigla());
			}
			
			if(tiposExamesAmostras.size() == 1) { //indica que todas amostras tem o mesmo tipo;
				messageCodeRetorno = AgruparExamesONMessageCode.AMOSTRAS_AGRUPADAS_SUCESSO;
			} else {
				messageCodeRetorno = AgruparExamesONMessageCode.AMOSTRAS_AGRUPADAS_ERRO;
			}
		}
		
		return messageCodeRetorno;
	}
	
	private String formatarMaterial(AmostraVO amostraVO) {
		StringBuilder materialFormatado = new StringBuilder();
		
		String numeroAmostra = String.format("%03d", amostraVO.getMaterialAmostra().getNumeroAmostra());
		
		materialFormatado.append(numeroAmostra).append(' ');
		
		if(amostraVO.getMaterialAmostra().getRegiaoAnatomica() != null) {
			materialFormatado.append(amostraVO.getMaterialAmostra().getRegiaoAnatomica());
			if(amostraVO.getMaterialAmostra().getDescricaoMaterial() != null) {
				materialFormatado.append(amostraVO.getMaterialAmostra().getDescricaoMaterial());
			} else {
				materialFormatado.append(amostraVO.getMaterialAmostra().getMaterialAnalise().getDescricao());
			}
			materialFormatado.append(_HIFEN_);
		} else {
			materialFormatado.append("- ");

			if(amostraVO.getMaterialAmostra().getDescricaoMaterial() != null) {
				materialFormatado.append(amostraVO.getMaterialAmostra().getDescricaoMaterial());
			} else {
				materialFormatado.append(amostraVO.getMaterialAmostra().getMaterialAnalise().getDescricao());
			}
		}
		
		return materialFormatado.toString();
	}

	public void excluirPatologista(List<AmostraVO> listaAmostras, AelPatologista patologistaResponsavelSelecionado) {
		for (AmostraVO amostraVO : listaAmostras) {
			if (amostraVO.isSelecionado() && amostraVO.getPatologistasResponsaveis() != null && !amostraVO.getPatologistasResponsaveis().isEmpty()) {
				final List<AelPatologista> novaLista = new ArrayList<AelPatologista>();
				for (final AelPatologista patologista : amostraVO.getPatologistasResponsaveis()) {
					if (!patologista.getSeq().equals(patologistaResponsavelSelecionado.getSeq())) {
						novaLista.add(patologista);
					}
				}
				amostraVO.setPatologistasResponsaveis(novaLista);
			}
		}
	}

	public void confirmarPatologistasResponsaveis(final List<AmostraVO> listaAmostraSelecionadas, final AelPatologista patologistaResponsavel) {
		if (patologistaResponsavel != null) {
			final String HIFEN_COM_ESPACO = " - ";
			final String QUEBRA_LINHA = "\n";
			for (AmostraVO amostraVO : listaAmostraSelecionadas) {
				if (amostraVO.isSelecionado()) {
					if (amostraVO.getPatologistasResponsaveis() == null) {
						amostraVO.setPatologistasResponsaveis(new ArrayList<AelPatologista>());
					}
					amostraVO.getPatologistasResponsaveis().add(patologistaResponsavel);
					final StringBuilder patologista = amostraVO.getPatologista() == null ? new StringBuilder() : new StringBuilder(amostraVO.getPatologista());
					if (patologista.length() > 0) {
						patologista.append(QUEBRA_LINHA);
					}
					patologista.append(patologistaResponsavel.getNome())
					.append(HIFEN_COM_ESPACO)
					.append(patologistaResponsavel.getFuncao().getDescricao());

					amostraVO.setPatologista(patologista.toString());
				}
			}
		}
	}
	
	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return this.aelApXPatologistaDAO;
	}

	protected AgruparExamesRN getAgruparExameRN() {
		return this.agruparExamesRN;
	}
		
}
