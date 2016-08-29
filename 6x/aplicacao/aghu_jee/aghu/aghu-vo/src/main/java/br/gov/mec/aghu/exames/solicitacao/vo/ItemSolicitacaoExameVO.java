package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import br.gov.mec.aghu.dominio.DominioFormaRespiracao;
import br.gov.mec.aghu.dominio.DominioOutrosFarmacos;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelTmpIntervaloColeta;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.core.persistence.BaseEntity;


/**
 * Representa um Item de Solicitacao de Exame. Na tela de Solicitar Exames.
 * 
 * @author rcorvalao
 *
 */
@SuppressWarnings({"PMD.ExcessiveClassLength", "PMD.ProperCloneImplementation"})
public class ItemSolicitacaoExameVO implements BaseEntity, Serializable, Cloneable {
	private static final long serialVersionUID = -5742411034859685425L;

	/**
	 * Quando for edicao esta propriedade deve esta preenchida como a entidade em edicao.
	 * Atributo central deste VO. NAO deve ter get e set. Usar setmodel e getmodel.
	 */
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	
	/**
	 * Dados da tela de solicitação de exames
	 */
	private SolicitacaoExameVO solicitacaoExameVO; 
	
	private Short sequencial;
	
	private UnfExecutaSinonimoExameVO unfExecutaExame;
	private Boolean urgente = Boolean.FALSE;
	private Boolean permiteUrgente = Boolean.FALSE;
	private Date dataProgramada;
	private Boolean calendar = Boolean.TRUE;
	private AelSitItemSolicitacoes situacaoCodigo;
	
	private AelTmpIntervaloColeta tmpIntervaloColeta;

	//#2253
	private Integer numeroAmostra;
	private Date intervaloHoras;
	private Integer intervaloDias;
	
	//#2250
	private DominioTipoTransporte tipoTransporte;
	private DominioSimNao oxigenioTransporte;
	
	//#2255
	private DominioFormaRespiracao formaRespiracao;
	private BigDecimal litrosOxigenio;
	private Integer percOxigenio;
	private Boolean readOnlyLitroOxigenios = Boolean.FALSE;
	private Boolean readOnlyPercOxigenios = Boolean.FALSE;
	
	//#2254
	private DominioOutrosFarmacos cadastroRegiaoAnatomica;
	private Boolean isCadastroRegiaoAnatomica = Boolean.FALSE;
	private Boolean isExigeDescMatAnls = Boolean.FALSE;
	private Boolean isExigeRegiaoAnatomica = Boolean.FALSE;
	
	//#5087
	private Boolean indGeradoAutomatico = Boolean.FALSE;
	private Boolean exameOpcionalSelecionado = Boolean.FALSE;
	private Boolean indTicketPacImp;
	private Boolean indInfComplImp;
	private String descRegiaoAnatomica;
	private String descMaterialAnalise;
	private AelRegiaoAnatomica regiaoAnatomica;
	
	//#2249
	private List<AelRecomendacaoExame> recomendacaoExameList;
	
	//Flags para aparecerem ou não as abas internas dependendo do exame selecionado
	private Boolean mostrarAbaTipoTransporte = Boolean.TRUE;
	private Boolean mostrarAbaIntervColeta = Boolean.TRUE;
	private Boolean mostrarAbaNoAmostras = Boolean.TRUE;
	private Boolean mostrarAbaConcentO2 = Boolean.TRUE;
	private Boolean mostrarAbaRegMatAnalise = Boolean.FALSE;
	private Boolean mostrarAbaRecomendacoes = Boolean.FALSE;
	private Boolean mostrarAbaExamesOpcionais = Boolean.TRUE;
	private Boolean mostrarAbaQuestionario = Boolean.FALSE;
	private Boolean mostrarAbaQuestionarioSismama = Boolean.FALSE;
	private Boolean mostrarAbaQuestionarioSismamaBiopsia = Boolean.FALSE;
	private Boolean mostrarAbaSituacao = Boolean.TRUE;
	
	
	private Boolean emEdicao = Boolean.FALSE;
	private String styleClass;
	private Boolean ehDependenteObrigatorio = Boolean.FALSE;
	private Boolean ehDependenteOpcional = Boolean.FALSE;
	
	//Listas de exames dependentes
	private List<ItemSolicitacaoExameVO> dependentesObrigratorios;
	private List<ItemSolicitacaoExameVO> dependentesOpcionais;
	private List<AelRespostaQuestao> respostasQuestoes;
	private List<AelGrupoQuestao> gruposQuestao;
	private List<AghCid> aghCids;
	private List<AelQuestionarios> questionarios;
	private Map<String, Object> questionarioSismama = new HashMap<String, Object>();
	private Map<String, Object> questionarioSismamaBiopsia = new HashMap<String, Object>();

	private Integer numeroAmostraAnterior;
	
    private boolean gravaDataAtual = false;

	private short seqp;

	private int soeSeq;
	
	
	public ItemSolicitacaoExameVO() {
		
	}
	
	public ItemSolicitacaoExameVO(AelItemSolicitacaoExames item) {
		this.itemSolicitacaoExame = item;
		this.setModel(item);
	}

	public ItemSolicitacaoExameVO(ItemSolicitacaoExameVO vo) {
		this.doCopiaPropriedades(vo);
	}
	
	public void doCopiaPropriedades(ItemSolicitacaoExameVO vo) {
		this.setSolicitacaoExameVO(vo.getSolicitacaoExameVO());
		this.setCalendar(vo.getCalendar());
		this.setDataProgramada(vo.getDataProgramada());
		this.setEmEdicao(vo.getEmEdicao());
		this.setFormaRespiracao(vo.getFormaRespiracao());
		this.setIntervaloDias(vo.getIntervaloDias());
		this.setIntervaloHoras(vo.getIntervaloHoras());
		this.setLitrosOxigenio(vo.getLitrosOxigenio());
		this.setMostrarAbaConcentO2(vo.getMostrarAbaConcentO2());
		this.setMostrarAbaIntervColeta(vo.getMostrarAbaIntervColeta());
		this.setMostrarAbaNoAmostras(vo.getMostrarAbaNoAmostras());
		this.setMostrarAbaRegMatAnalise(vo.getMostrarAbaRegMatAnalise());
		this.setMostrarAbaTipoTransporte(vo.getMostrarAbaTipoTransporte());
		this.setMostrarAbaExamesOpcionais(vo.getMostrarAbaExamesOpcionais());
		this.setMostrarAbaQuestionario(vo.getMostrarAbaQuestionario());
		this.setNumeroAmostra(vo.getNumeroAmostra());
		this.setOxigenioTransporte(vo.getOxigenioTransporte());
		this.setPercOxigenio(vo.getPercOxigenio());
		this.setReadOnlyLitroOxigenios(vo.getReadOnlyLitroOxigenios());
		this.setReadOnlyPercOxigenios(vo.getReadOnlyPercOxigenios());
		this.setSequencial(vo.getSequencial());
		this.setSituacaoCodigo(vo.getSituacaoCodigo());
		this.setTipoTransporte(vo.getTipoTransporte());
		this.setTmpIntervaloColeta(vo.getTmpIntervaloColeta());
		this.setUnfExecutaExame(vo.getUnfExecutaExame());
		this.setUrgente(vo.getUrgente());
		this.setPermiteUrgente(vo.getPermiteUrgente());
		this.setIndGeradoAutomatico(vo.getIndGeradoAutomatico());
		this.setExameOpcionalSelecionado(vo.getExameOpcionalSelecionado());
		this.setIndTicketPacImp(vo.getIndTicketPacImp());
		this.setIndInfComplImp(vo.getIndInfComplImp());
		this.setDescRegiaoAnatomica(vo.getDescRegiaoAnatomica());
		this.setDescMaterialAnalise(vo.getDescMaterialAnalise());
		this.setRegiaoAnatomica(vo.getRegiaoAnatomica());
		this.setRecomendacaoExameList(vo.getRecomendacaoExameList());
		this.setMostrarAbaRecomendacoes(vo.getMostrarAbaRecomendacoes());
		this.setCadastroRegiaoAnatomica(vo.getCadastroRegiaoAnatomica());
		this.setIsCadastroRegiaoAnatomica(vo.getIsCadastroRegiaoAnatomica());
		this.setIsExigeDescMatAnls(vo.getIsExigeDescMatAnls());
		this.setDependentesObrigratorios(vo.getDependentesObrigratorios());
		this.setDependentesOpcionais(vo.getDependentesOpcionais());
		this.setStyleClass(vo.getStyleClass());
		this.setEhDependenteObrigatorio(vo.getEhDependenteObrigatorio());
		this.setEhDependenteOpcional(vo.getEhDependenteOpcional());
		this.setIsExigeRegiaoAnatomica(vo.getIsExigeRegiaoAnatomica());
		this.setRespostasQuestoes(vo.getRespostasQuestoes());
		this.setGruposQuestao(vo.getGruposQuestao());
		this.setMostrarAbaQuestionarioSismama(vo.getMostrarAbaQuestionarioSismama());
		this.setQuestionarioSismama(vo.getQuestionarioSismama());
		this.setQuestionarioSismamaBiopsia(vo.getQuestionarioSismamaBiopsia());
	}


	@Override
	public Object clone() throws CloneNotSupportedException {

		ItemSolicitacaoExameVO copia = new ItemSolicitacaoExameVO();

		copia.setItemSolicitacaoExame((AelItemSolicitacaoExames) clonar(itemSolicitacaoExame));
		copia.setSolicitacaoExameVO(solicitacaoExameVO);
		copia.setSequencial(sequencial);
		copia.setUnfExecutaExame((UnfExecutaSinonimoExameVO) ItemSolicitacaoExameVO.clonar(unfExecutaExame));
		copia.setUrgente(urgente);
		copia.setDataProgramada(dataProgramada);
		copia.setCalendar(calendar);
		copia.setSituacaoCodigo((AelSitItemSolicitacoes) ItemSolicitacaoExameVO.clonar(situacaoCodigo));
		copia.setTmpIntervaloColeta((AelTmpIntervaloColeta) ItemSolicitacaoExameVO.clonar(tmpIntervaloColeta));
		copia.setNumeroAmostra(numeroAmostra);
		copia.setIntervaloHoras(intervaloHoras);
		copia.setIntervaloDias(intervaloDias);
		copia.setTipoTransporte(tipoTransporte);
		copia.setOxigenioTransporte(oxigenioTransporte);
		copia.setFormaRespiracao(formaRespiracao);
		copia.setLitrosOxigenio(litrosOxigenio);
		copia.setPercOxigenio(percOxigenio);
		copia.setReadOnlyLitroOxigenios(readOnlyLitroOxigenios);
		copia.setReadOnlyPercOxigenios(readOnlyPercOxigenios);
		copia.setCadastroRegiaoAnatomica(cadastroRegiaoAnatomica);
		copia.setIsCadastroRegiaoAnatomica(isCadastroRegiaoAnatomica);
		copia.setIsExigeDescMatAnls(isExigeDescMatAnls);
		copia.setIsExigeRegiaoAnatomica(isExigeRegiaoAnatomica);
		copia.setIndGeradoAutomatico(indGeradoAutomatico);
		copia.setExameOpcionalSelecionado(exameOpcionalSelecionado);
		copia.setIndTicketPacImp(indTicketPacImp);
		copia.setIndInfComplImp(indInfComplImp);
		copia.setDescRegiaoAnatomica(descRegiaoAnatomica);
		copia.setDescMaterialAnalise(descMaterialAnalise);
		copia.setRegiaoAnatomica((AelRegiaoAnatomica) ItemSolicitacaoExameVO.clonar(regiaoAnatomica));
		copia.setRecomendacaoExameList(recomendacaoExameList); //
		copia.setMostrarAbaTipoTransporte(mostrarAbaTipoTransporte);
		copia.setMostrarAbaIntervColeta(mostrarAbaIntervColeta);
		copia.setMostrarAbaNoAmostras(mostrarAbaNoAmostras);
		copia.setMostrarAbaConcentO2(mostrarAbaConcentO2);
		copia.setMostrarAbaRegMatAnalise(mostrarAbaRegMatAnalise);
		copia.setMostrarAbaRecomendacoes(mostrarAbaRecomendacoes);
		copia.setMostrarAbaExamesOpcionais(mostrarAbaExamesOpcionais);
		copia.setMostrarAbaQuestionario(mostrarAbaQuestionario);
		copia.setMostrarAbaQuestionarioSismama(mostrarAbaQuestionarioSismama);
		copia.setMostrarAbaQuestionarioSismamaBiopsia(mostrarAbaQuestionarioSismamaBiopsia);
		copia.setMostrarAbaSituacao(mostrarAbaSituacao);
		copia.setEmEdicao(emEdicao);
		copia.setStyleClass(styleClass);
		copia.setEhDependenteObrigatorio(ehDependenteObrigatorio);
		copia.setEhDependenteOpcional(ehDependenteOpcional);
		copia.setDependentesObrigratorios(clonarLista(dependentesObrigratorios));
		copia.setDependentesOpcionais(clonarLista(dependentesOpcionais));
		copia.setRespostasQuestoes(respostasQuestoes);
		copia.setGruposQuestao(gruposQuestao);
		copia.setAghCids(aghCids);
		copia.setQuestionarios(questionarios);
		copia.setQuestionarioSismama(questionarioSismama);
		copia.setQuestionarioSismamaBiopsia(questionarioSismamaBiopsia);
		copia.setGravaDataAtual(gravaDataAtual);	

		return copia;
	}
	
	public static Object clonar(Object obj) throws CloneNotSupportedException {

		if (obj == null) {
			return null;
		}

		try {
			return BeanUtils.cloneBean(obj);
		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
			throw new CloneNotSupportedException();
		}
	}
	
	public static List<ItemSolicitacaoExameVO> clonarLista(List<ItemSolicitacaoExameVO> lista) throws CloneNotSupportedException {

		if (lista == null) {
			return null;
		}

		List<ItemSolicitacaoExameVO> copia = new LinkedList<ItemSolicitacaoExameVO>();

		for (ItemSolicitacaoExameVO itemOrig : lista) {

			ItemSolicitacaoExameVO item = (ItemSolicitacaoExameVO) itemOrig.clone();
			copia.add(item);
		}

		return copia;
	}
	
	public String getUrgenteDescricao() {
		DominioSimNao s = DominioSimNao.getInstance(this.getUrgente());
		return s.getDescricao();
	}
	
	public void setModel(AelItemSolicitacaoExames item) {
		if (item == null) {
			throw new IllegalArgumentException("Item solicitacao exame nao informado!!!");
		}
		this.itemSolicitacaoExame = item;
		
		this.setSequencial(item.getId().getSeqp());
		this.setEmEdicao(Boolean.FALSE);
		this.setIndGeradoAutomatico(item.getIndGeradoAutomatico());
		
		this.setUnfExecutaExame(new UnfExecutaSinonimoExameVO(item.getAelUnfExecutaExames()));
		this.setTipoColeta(item.getTipoColeta()); // Atributo urgente		
		this.setDataProgramada(item.getDthrProgramada());
		this.setSituacaoCodigo(item.getSituacaoItemSolicitacao());
		// Aba tipo transporte
		this.setTipoTransporte(item.getTipoTransporte());
		this.setOxigenioTransporte(DominioSimNao.getInstance(item.getIndUsoO2()));
		// Aba intervalo coleta
		this.setTmpIntervaloColeta(new AelTmpIntervaloColeta(item.getIntervaloColeta()));
		// Aba numero amostra
		this.setIntervaloDias(item.getIntervaloDias() != null ? item.getIntervaloDias().intValue() : null);
		this.setIntervaloHoras(item.getIntervaloHoras());
		this.setNumeroAmostra(item.getNroAmostras() != null ? item.getNroAmostras().intValue() : null);
		// Aba concentracao o2
		this.setFormaRespiracao(item.getFormaRespiracao());
		this.setPercOxigenio(item.getPercOxigenio() != null ? item.getPercOxigenio().intValue() : null);
		this.setLitrosOxigenio(item.getLitrosOxigenio());
		// Outros		
		this.setIndTicketPacImp(item.getIndTicketPacImp());
		this.setIndInfComplImp(item.getIndInfComplImp());
		this.setDescRegiaoAnatomica(item.getDescRegiaoAnatomica());
		this.setDescMaterialAnalise(item.getDescMaterialAnalise());
		this.setRegiaoAnatomica(item.getRegiaoAnatomica());
		
		// Listas nao usadas na edicao.
//		this.setRecomendacaoExameList(vo.getRecomendacaoExameList());
//		this.setDependentesObrigratorios(vo.getDependentesObrigratorios());
//		this.setDependentesOpcionais(vo.getDependentesOpcionais());
		
		// Atributos calculados. Internamente.
		this.setReadOnlyLitroOxigenios(item.getLitrosOxigenio() == null);
		this.setReadOnlyPercOxigenios(item.getPercOxigenio() == null);
		
		// TODO Para edicao todo o trecho de codigo abaixo deve ser revisado.
//		this.setCadastroRegiaoAnatomica(vo.getCadastroRegiaoAnatomica());
//		this.setIsCadastroRegiaoAnatomica(vo.getIsCadastroRegiaoAnatomica());
//		this.setIsExigeDescMatAnls(vo.getIsExigeDescMatAnls());
//		this.setEhDependenteObrigatorio(vo.getEhDependenteObrigatorio());
//		this.setEhDependenteOpcional(vo.getEhDependenteOpcional());
				
		// Atributos calculados. Externamente.
		this.setCalendar(Boolean.TRUE);
		this.setMostrarAbaConcentO2(Boolean.TRUE);
		this.setMostrarAbaIntervColeta(Boolean.TRUE);
		this.setMostrarAbaNoAmostras(Boolean.TRUE);
		this.setMostrarAbaRegMatAnalise(Boolean.TRUE);
		this.setMostrarAbaTipoTransporte(Boolean.TRUE);
		this.setMostrarAbaRecomendacoes(Boolean.TRUE);
		this.setMostrarAbaQuestionario(Boolean.TRUE);
		this.setMostrarAbaSituacao(Boolean.TRUE);
		this.setStyleClass("");
	}

    private void validaDataHoraProgramada() {
        if(!gravaDataAtual) {
            itemSolicitacaoExame.setDthrProgramada(this.getDataProgramada());
        } else {
            itemSolicitacaoExame.setDthrProgramada(new Date());
        }
    }

	public AelItemSolicitacaoExames getModel() {
		if (this.getUnfExecutaExame() == null) {
			throw new IllegalArgumentException("Exame nao foi informado corretamente.");
		}
		
		itemSolicitacaoExame = new AelItemSolicitacaoExames();
		
		itemSolicitacaoExame.setTipoColeta(this.getTipoColeta());

        validaDataHoraProgramada();

		itemSolicitacaoExame.setSituacaoItemSolicitacao(this.getSituacaoCodigo());
		if (this.getUnfExecutaExame().getUnfExecutaExame() != null) {
			itemSolicitacaoExame.setAelUnfExecutaExames(this.getUnfExecutaExame().getUnfExecutaExame());
			if (this.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise() != null) {
				itemSolicitacaoExame.setExame(
						this.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelExames()
				);
				itemSolicitacaoExame.setMaterialAnalise(
						this.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises()
				);
				itemSolicitacaoExame.setAelExameMaterialAnalise(this.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise());
			}
			if (this.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional() != null) {
				itemSolicitacaoExame.setUnidadeFuncional(this.getUnfExecutaExame().getUnfExecutaExame().getUnidadeFuncional());
			}
		}
		
		if (this.getMostrarAbaTipoTransporte()) {
			itemSolicitacaoExame.setTipoTransporte(this.getTipoTransporte());
			itemSolicitacaoExame.setIndUsoO2(this.getOxigenioTransporte().isSim());	
		}
		
		if (this.getMostrarAbaIntervColeta()) {
			if (this.getTmpIntervaloColeta() != null) {
				itemSolicitacaoExame.setIntervaloColeta(this.getTmpIntervaloColeta().getIntervaloColeta());
			}
		}
		
		if (this.getMostrarAbaNoAmostras()) {
			if (this.getNumeroAmostra() != null) {
				itemSolicitacaoExame.setNroAmostras(this.getNumeroAmostra().byteValue());
			}
			if (this.getIntervaloDias() != null) {
				itemSolicitacaoExame.setIntervaloDias(this.getIntervaloDias().byteValue());
			}
			itemSolicitacaoExame.setIntervaloHoras(this.getIntervaloHoras());
		}
		
		if (this.getMostrarAbaConcentO2()) {
			itemSolicitacaoExame.setFormaRespiracao(this.getFormaRespiracao());
			itemSolicitacaoExame.setLitrosOxigenio(this.getLitrosOxigenio());
			if (this.getPercOxigenio() != null) {
				itemSolicitacaoExame.setPercOxigenio(this.getPercOxigenio().shortValue());
			}
		}
		
		itemSolicitacaoExame.setIndGeradoAutomatico(this.getIndGeradoAutomatico());
		
		itemSolicitacaoExame.setIndTicketPacImp(this.getIndTicketPacImp());
		itemSolicitacaoExame.setIndInfComplImp(this.getIndInfComplImp());
		itemSolicitacaoExame.setDescMaterialAnalise(this.getDescMaterialAnalise());
		itemSolicitacaoExame.setDescRegiaoAnatomica(this.getDescRegiaoAnatomica());
		
		itemSolicitacaoExame.setRegiaoAnatomica(this.getRegiaoAnatomica());
		
		// Pripriedades setadas quando da associacao com a SolicitacaoExame.
		//itemSolicitacaoExame.setSolicitacaoExame();
		//itemSolicitacaoExame.setServidorResponsabilidade();
		
		// #2257
		this.atualizarRespostas(itemSolicitacaoExame);

		// FIM #2257
		
		return itemSolicitacaoExame;
	}
	
	private void atualizarRespostas(final AelItemSolicitacaoExames itemSolicitacaoExame) {
		if(this.getSequencial() != null) {
			itemSolicitacaoExame.setIndexOrigem(this.getSequencial().intValue());
		}
		if (this.getGruposQuestao() != null && !this.getGruposQuestao().isEmpty()) {
			itemSolicitacaoExame.setAelRespostasQuestoes(new ArrayList<AelRespostaQuestao>());
			for (final AelGrupoQuestao grupo : this.getGruposQuestao()) {
				for (final AelRespostaQuestao resposta : grupo.getAelRespostaQuestaos()) {
					resposta.setAelItemSolicitacaoExames(itemSolicitacaoExame);
					itemSolicitacaoExame.getAelRespostasQuestoes().add(resposta);
				}
			}
		}
	}
	
	
	public UnfExecutaSinonimoExameVO getUnfExecutaExame() {
		return unfExecutaExame;
	}
	
	public void setUnfExecutaExame(UnfExecutaSinonimoExameVO u) {
		this.unfExecutaExame = u;
	}
	
	public Boolean getUrgente() {
		return urgente;
	}

	public void setUrgente(Boolean urgente) {
		this.urgente = urgente;
	}
	
	public Date getDataProgramada() {
		return dataProgramada;
	}

	public void setDataProgramada(Date dataProgramada) {
		this.dataProgramada = dataProgramada;
	}
	
	public Boolean getCalendar() {
		return calendar;
	}

	public void setCalendar(Boolean calendar) {
		this.calendar = calendar;
	}
	
	
	
	public DominioTipoTransporte getTipoTransporte() {
		return tipoTransporte;
	}

	public void setTipoTransporte(DominioTipoTransporte tipoTransporte) {
		this.tipoTransporte = tipoTransporte;
	}
	
	/**
	 * IndUsoO2
	 * @return
	 */
	public DominioSimNao getOxigenioTransporte() {
		return oxigenioTransporte;
	}
	
	/**
	 * IndUsoO2
	 * @param oxigenioTransporte
	 */
	public void setOxigenioTransporte(DominioSimNao oxigenioTransporte) {
		this.oxigenioTransporte = oxigenioTransporte;
	}
	

	public AelTmpIntervaloColeta getTmpIntervaloColeta() {
		return tmpIntervaloColeta;
	}

	public void setTmpIntervaloColeta(AelTmpIntervaloColeta tmpIntervaloColeta) {
		this.tmpIntervaloColeta = tmpIntervaloColeta;
	}

	/**
	 * @param situacaoCodigo the situacaoCodigo to set
	 */
	public void setSituacaoCodigo(AelSitItemSolicitacoes situacaoCodigo) {
		this.situacaoCodigo = situacaoCodigo;
	}

	/**
	 * @return the situacaoCodigo
	 */
	public AelSitItemSolicitacoes getSituacaoCodigo() {
		return situacaoCodigo;
	}

	public Integer getNumeroAmostra() {
		return numeroAmostra;
	}

	public void setNumeroAmostra(Integer numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}

	public Date getIntervaloHoras() {
		return intervaloHoras;
	}

	public void setIntervaloHoras(Date intervaloHoras) {
		this.intervaloHoras = intervaloHoras;
	}

	public Integer getIntervaloDias() {
		return intervaloDias;
	}

	public void setIntervaloDias(Integer intervaloDias) {
		this.intervaloDias = intervaloDias;
	}

	public DominioFormaRespiracao getFormaRespiracao() {
		return formaRespiracao;
	}

	public void setFormaRespiracao(DominioFormaRespiracao formaRespiracao) {
		this.formaRespiracao = formaRespiracao;
	}

	public BigDecimal getLitrosOxigenio() {
		return litrosOxigenio;
	}

	public void setLitrosOxigenio(BigDecimal litrosOxigenio) {
		this.litrosOxigenio = litrosOxigenio;
	}

	public Integer getPercOxigenio() {
		return percOxigenio;
	}

	public void setPercOxigenio(Integer percOxigenio) {
		this.percOxigenio = percOxigenio;
	}

	public Boolean getReadOnlyLitroOxigenios() {
		this.readOnlyLitroOxigenios = Boolean.TRUE;

		if(DominioFormaRespiracao.DOIS == this.getFormaRespiracao()) {
			this.readOnlyLitroOxigenios = Boolean.FALSE;
		}
		if(DominioFormaRespiracao.TRES == this.getFormaRespiracao()) {
			this.readOnlyLitroOxigenios = Boolean.TRUE;
			this.setLitrosOxigenio(null);
		}
		return readOnlyLitroOxigenios;
	}

	public void setReadOnlyLitroOxigenios(Boolean readOnlyLitroOxigenios) {
		this.readOnlyLitroOxigenios = readOnlyLitroOxigenios;
	}

	public Boolean getReadOnlyPercOxigenios() {
		this.readOnlyPercOxigenios = Boolean.TRUE;

		if(DominioFormaRespiracao.DOIS == this.getFormaRespiracao()) {
			this.readOnlyPercOxigenios = Boolean.TRUE;
			this.setPercOxigenio(null);
		}

		if(DominioFormaRespiracao.TRES == this.getFormaRespiracao()) {
			this.readOnlyPercOxigenios = Boolean.FALSE;
		}
		
		return readOnlyPercOxigenios;
	}

	public void setReadOnlyPercOxigenios(Boolean readOnlyPercOxigenios) {
		this.readOnlyPercOxigenios = readOnlyPercOxigenios;
	}

	/**
	 * pus.ind_exige_transporte_o2
	 * @return
	 */
	public Boolean getMostrarAbaTipoTransporte() {
		return mostrarAbaTipoTransporte;
	}

	public void setMostrarAbaTipoTransporte(Boolean mostrarAbaTipoTransporte) {
		this.mostrarAbaTipoTransporte = mostrarAbaTipoTransporte;
	}
	
	/**
	 * ema.ind_usa_intervalo_cadastrado
	 * @return
	 */
	public Boolean getMostrarAbaIntervColeta() {
		return mostrarAbaIntervColeta;
	}

	public void setMostrarAbaIntervColeta(Boolean mostrarAbaIntervColeta) {
		this.mostrarAbaIntervColeta = mostrarAbaIntervColeta;
	}
	
	/**
	 * ema.ind_solic_informa_coletas
	 * @return
	 */
	public Boolean getMostrarAbaNoAmostras() {
		return mostrarAbaNoAmostras;
	}

	public void setMostrarAbaNoAmostras(Boolean mostrarAbaNoAmostras) {
		this.mostrarAbaNoAmostras = mostrarAbaNoAmostras;
	}
	
	/**
	 * ema.ind_forma_respiracao
	 * @return
	 */
	public Boolean getMostrarAbaConcentO2() {
		return mostrarAbaConcentO2;
	}

	public void setMostrarAbaConcentO2(Boolean mostrarAbaConcentO2) {
		this.mostrarAbaConcentO2 = mostrarAbaConcentO2;
	}
	
	/**
	 * ema.ind_exige_regiao_anatomica ou eh material analise diverso. 
	 * @return
	 */
	public Boolean getMostrarAbaRegMatAnalise() {
		return mostrarAbaRegMatAnalise;
	}

	public void setMostrarAbaRegMatAnalise(Boolean mostrarAbaRegMatAnalise) {
		this.mostrarAbaRegMatAnalise = mostrarAbaRegMatAnalise;
	}

	/**
	 * @param emEdicao the emEdicao to set
	 */
	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	/**
	 * @return the emEdicao
	 */
	public Boolean getEmEdicao() {
		return emEdicao;
	}

	/**
	 * @param sequencial the sequencial to set
	 */
	public void setSequencial(Short s) {
		this.sequencial = s;
	}

	/**
	 * @return the sequencial
	 */
	public Short getSequencial() {
		return sequencial;
	}

	

	public SolicitacaoExameVO getSolicitacaoExameVO() {
		return solicitacaoExameVO;
	}

	public void setSolicitacaoExameVO(SolicitacaoExameVO solicitacaoExameVO) {
		this.solicitacaoExameVO = solicitacaoExameVO;
	}

	public DominioOutrosFarmacos getCadastroRegiaoAnatomica() {
		return cadastroRegiaoAnatomica;
	}

	public void setCadastroRegiaoAnatomica(
			DominioOutrosFarmacos cadastroRegiaoAnatomica) {
		this.cadastroRegiaoAnatomica = cadastroRegiaoAnatomica;
	}

	public Boolean getIsCadastroRegiaoAnatomica() {
		return isCadastroRegiaoAnatomica;
	}

	public void setIsCadastroRegiaoAnatomica(Boolean isCadastroRegiaoAnatomica) {
		this.isCadastroRegiaoAnatomica = isCadastroRegiaoAnatomica;
	}
	
	
	public void setIndGeradoAutomatico(Boolean indGeradoAutomatico) {
		this.indGeradoAutomatico = indGeradoAutomatico;
	}
	
	public Boolean getIndGeradoAutomatico() {
		return indGeradoAutomatico;
	}
	
	public void setIndTicketPacImp(Boolean indTicketPacImp) {
		this.indTicketPacImp = indTicketPacImp;
	}
	
	public Boolean getIndTicketPacImp() {
		return indTicketPacImp;
	}
	
	public void setIndInfComplImp(Boolean indInfComplImp) {
		this.indInfComplImp = indInfComplImp;
	}
	
	public Boolean getIndInfComplImp() {
		return indInfComplImp;
	}
	
	public void setDescRegiaoAnatomica(String descRegiaoAnatomica) {
		this.descRegiaoAnatomica = descRegiaoAnatomica;
	}
	
	public String getDescRegiaoAnatomica() {
		return descRegiaoAnatomica;
	}
	
	public void setDescMaterialAnalise(String descMaterialAnalise) {
		this.descMaterialAnalise = descMaterialAnalise;
	}
	
	public String getDescMaterialAnalise() {
		return descMaterialAnalise;
	}
	
	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.setUrgente(tipoColeta != null && DominioTipoColeta.U == tipoColeta);
	}
	
	public DominioTipoColeta getTipoColeta() {
		return (this.getUrgente() ? DominioTipoColeta.U : DominioTipoColeta.N);
	}
	
	public void setRegiaoAnatomica(AelRegiaoAnatomica regiaoAnatomica) {
		this.regiaoAnatomica = regiaoAnatomica;
	}
	
	public AelRegiaoAnatomica getRegiaoAnatomica() {
		return regiaoAnatomica;
	}

	public void setDependentesObrigratorios(
			List<ItemSolicitacaoExameVO> dependentesObrigratorios) {
		this.dependentesObrigratorios = dependentesObrigratorios;
	}
	
	public List<ItemSolicitacaoExameVO> getDependentesObrigratorios() {
		if(dependentesObrigratorios == null) {
			dependentesObrigratorios = new ArrayList<ItemSolicitacaoExameVO>();
		}
		return dependentesObrigratorios;
	}
	
	public void setDependentesOpcionais(
			List<ItemSolicitacaoExameVO> dependentesOpcionais) {
		this.dependentesOpcionais = dependentesOpcionais;
	}
	
	public List<ItemSolicitacaoExameVO> getDependentesOpcionais() {
		if(dependentesOpcionais == null) {
			dependentesOpcionais = new ArrayList<ItemSolicitacaoExameVO>();
		}
		return dependentesOpcionais;
	}

	public Boolean getIsExigeDescMatAnls() {
		return isExigeDescMatAnls;
	}

	public void setIsExigeDescMatAnls(Boolean isExigeDescMatAnls) {
		this.isExigeDescMatAnls = isExigeDescMatAnls;
	}

	public List<AelRecomendacaoExame> getRecomendacaoExameList() {
		return recomendacaoExameList;
	}

	public void setRecomendacaoExameList(
			List<AelRecomendacaoExame> recomendacaoExameList) {
		this.recomendacaoExameList = recomendacaoExameList;
	}

	public Boolean getMostrarAbaRecomendacoes() {
		return mostrarAbaRecomendacoes;
	}

	public void setMostrarAbaRecomendacoes(Boolean mostrarAbaRecomendacoes) {
		this.mostrarAbaRecomendacoes = mostrarAbaRecomendacoes;
	}
	
	public Boolean getMostrarAbaExamesOpcionais() {
		return mostrarAbaExamesOpcionais;
	}

	public void setMostrarAbaExamesOpcionais(Boolean mostrarAbaExamesOpcionais) {
		this.mostrarAbaExamesOpcionais = mostrarAbaExamesOpcionais;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setEhDependenteObrigatorio(Boolean ehDependenteObrigatorio) {
		this.ehDependenteObrigatorio = ehDependenteObrigatorio;
	}

	public Boolean getEhDependenteObrigatorio() {
		return ehDependenteObrigatorio;
	}

	public void setEhDependenteOpcional(Boolean ehDependenteOpcional) {
		this.ehDependenteOpcional = ehDependenteOpcional;
	}

	public Boolean getEhDependenteOpcional() {
		return ehDependenteOpcional;
	}

	public Boolean getIsExigeRegiaoAnatomica() {
		return isExigeRegiaoAnatomica;
	}

	public void setIsExigeRegiaoAnatomica(Boolean isExigeRegiaoAnatomica) {
		this.isExigeRegiaoAnatomica = isExigeRegiaoAnatomica;
	}

	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
		
		if (itemSolicitacaoExame != null && itemSolicitacaoExame.getId() != null){
			setSeqPGravado(itemSolicitacaoExame.getId().getSeqp());
			setSoeSeqGravado(itemSolicitacaoExame.getId().getSoeSeq());
		}
	}

	public Boolean getExameOpcionalSelecionado() {
		return exameOpcionalSelecionado;
	}

	public void setExameOpcionalSelecionado(Boolean exameOpcionalSelecionado) {
		this.exameOpcionalSelecionado = exameOpcionalSelecionado;
	}

	public void setMostrarAbaQuestionario(Boolean mostrarAbaQuestionario) {
		this.mostrarAbaQuestionario = mostrarAbaQuestionario;
	}

	public Boolean getMostrarAbaQuestionario() {
		return mostrarAbaQuestionario;
	}
	
	public Boolean getMostrarAbaQuestionarioSismama() {
		return mostrarAbaQuestionarioSismama;
	}

	public void setMostrarAbaQuestionarioSismama(Boolean mostrarAbaQuestionarioSismama) {
		this.mostrarAbaQuestionarioSismama = mostrarAbaQuestionarioSismama;
	}

	public void setRespostasQuestoes(List<AelRespostaQuestao> respostasQuestoes) {
		this.respostasQuestoes = respostasQuestoes;
	}

	public List<AelRespostaQuestao> getRespostasQuestoes() {
		return respostasQuestoes;
	}

	public void setGruposQuestao(List<AelGrupoQuestao> gruposQuestao) {
		this.gruposQuestao = gruposQuestao;
	}

	public List<AelGrupoQuestao> getGruposQuestao() {
		return gruposQuestao;
	}

	public void setAghCids(List<AghCid> aghCids) {
		this.aghCids = aghCids;
	}

	public List<AghCid> getAghCids() {
		return aghCids;
	}

	public void setQuestionarios(List<AelQuestionarios> questionarios) {
		this.questionarios = questionarios;
	}

	public List<AelQuestionarios> getQuestionarios() {
		return questionarios;
	}

	public Map<String, Object> getQuestionarioSismama() {
		return questionarioSismama;
	}

	public void setQuestionarioSismama(Map<String, Object> questionarioSismama) {
		this.questionarioSismama = questionarioSismama;
	}

	public Map<String, Object> getQuestionarioSismamaBiopsia() {
		return questionarioSismamaBiopsia;
	}

	public void setQuestionarioSismamaBiopsia(
			Map<String, Object> questionarioSismamaBiopsia) {
		this.questionarioSismamaBiopsia = questionarioSismamaBiopsia;
	}

	public Boolean getMostrarAbaQuestionarioSismamaBiopsia() {
		return mostrarAbaQuestionarioSismamaBiopsia;
	}

	public void setMostrarAbaQuestionarioSismamaBiopsia(
			Boolean mostrarAbaQuestionarioSismamaBiopsia) {
		this.mostrarAbaQuestionarioSismamaBiopsia = mostrarAbaQuestionarioSismamaBiopsia;
	}

	public Boolean getMostrarAbaSituacao() {
		return mostrarAbaSituacao;
	}

	public void setMostrarAbaSituacao(Boolean mostrarAbaSituacao) {
		this.mostrarAbaSituacao = mostrarAbaSituacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((itemSolicitacaoExame == null) ? 0 : itemSolicitacaoExame
						.hashCode());
		result = prime * result
				+ ((sequencial == null) ? 0 : sequencial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof ItemSolicitacaoExameVO)){
			return false;
		}	
		ItemSolicitacaoExameVO other = (ItemSolicitacaoExameVO) obj;
		if (itemSolicitacaoExame == null) {
			if (other.itemSolicitacaoExame != null){
				return false;
			}	
		} else if (!itemSolicitacaoExame.equals(other.itemSolicitacaoExame)){
			return false;
		}	
		if (sequencial == null) {
			if (other.sequencial != null){
				return false;
			}	
		} else if (!sequencial.equals(other.sequencial)){
			return false;
		}	
		return true;
	}

    public boolean isGravaDataAtual() {
        return gravaDataAtual;
    }

    public void setGravaDataAtual(boolean gravaDataAtual) {
        this.gravaDataAtual = gravaDataAtual;
    }

	public Integer getNumeroAmostraAnterior() {
		return numeroAmostraAnterior;
	}

	public void setNumeroAmostraAnterior(Integer numeroAmostraAnterior) {
		this.numeroAmostraAnterior = numeroAmostraAnterior;
	}

	public void setSeqPGravado(short seqp) {
		this.seqp = seqp;
		
	}

	public void setSoeSeqGravado(int soeSeq) {
		this.soeSeq = soeSeq;
	}

	
	public short getSeqp() {
		return seqp;
	}

	
	public int getSoeSeq() {
		return soeSeq;
	}

	
	public Boolean getPermiteUrgente() {
		return permiteUrgente;
	}

	
	public void setPermiteUrgente(Boolean permiteUrgente) {
		this.permiteUrgente = permiteUrgente;
	}

}