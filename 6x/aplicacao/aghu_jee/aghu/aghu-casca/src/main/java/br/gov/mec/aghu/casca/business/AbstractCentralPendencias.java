package br.gov.mec.aghu.casca.business;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Componente registrado em <code>components.xml</code>: 
 * <code>centralPendencias</code>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.PackagePrivateSeamContextsManager","PMD.AtributoEmSeamContextManager"})
public abstract class AbstractCentralPendencias
		extends
		BaseBusiness
		implements
			CentralPendenciasInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7184874540479118344L;
	protected static final PendenciaVO PENDENCIA_VAZIA = new PendenciaVO(
			TipoPendenciaEnum.INEXISTENTE,
			null,
			null);
	private Map<TipoPendenciaEnum, PendenciaVO> cacheMapa = null;
	private PendenciaVO topPendencia = null;

	/**
	 * Esta lista <b>NAO</b> pode conter elementos com tipos repetidos, isto eh,
	 * somente um elemento do tipo {@link TipoPendenciaEnum#BLOQUEANTE} (se for o caso),
	 * um do tipo {@link TipoPendenciaEnum#INTRUSIVA} e assim por diante.
	 * @return
	 */
	protected abstract List<PendenciaVO> getListaPendencias();

	protected void limparCacheMapa() {

		this.cacheMapa = null;
		this.topPendencia = null;
	}

	protected Map<TipoPendenciaEnum, PendenciaVO> getMapaPrioridadeVO()
			throws ApplicationBusinessException {

		List<PendenciaVO> lst = null;
		PendenciaVO old = null;

		if (this.cacheMapa == null) {
			lst = this.getListaPendencias();
			if ((lst != null) && !lst.isEmpty()) {
				this.cacheMapa = new HashMap<TipoPendenciaEnum, PendenciaVO>();
				for (PendenciaVO vo : lst) {
					old = this.cacheMapa.get(vo.getTipo());
					if (old != null) {
						this.limparCacheMapa();
						throw new ApplicationBusinessException(
								MessagesPendenciaEnum.PRIORIDADE_REPETIDA,
								old.getTipo(),
								old.getUrl());
					}
					this.cacheMapa.put(vo.getTipo(), vo);
				}
			} else {
				this.cacheMapa = Collections.emptyMap();
			}
		}

		return this.cacheMapa;
	}

	protected PendenciaVO getTopPendencia()
			throws ApplicationBusinessException {

		Map<TipoPendenciaEnum, PendenciaVO> mapa = null;

		if (this.topPendencia == null) {
			mapa = this.getMapaPrioridadeVO();
			for (TipoPendenciaEnum t : TipoPendenciaEnum.values()) {
				this.topPendencia = mapa.get(t);
				if (this.topPendencia != null) {
					break;
				}
			}
			if (this.topPendencia == null) {
				this.topPendencia = PENDENCIA_VAZIA;
			}
		}

		return this.topPendencia;
	}
	
	protected List<String> getKeyList(ResourceBundle bundle) {
		
		List<String> result = null;		
		Enumeration<String> keys = null;
		
		if (bundle != null) {
			keys = bundle.getKeys();
			if (keys != null) {
				result = new LinkedList<String>();
				while (keys.hasMoreElements()) {
					result.add(keys.nextElement());
				}
			}
		}
		
		return result;
	}
	
	protected AbstractCentralPendencias() {
		super();
	}

	@Override
	public TipoPendenciaEnum getTipoPendenciaMaiorPrioridade()
			throws ApplicationBusinessException {

		TipoPendenciaEnum result = null;
		PendenciaVO top = null;

		//limpa cache
		this.limparCacheMapa();
 		top = this.getTopPendencia();
		result = top.getTipo();

		return result;
	}

	@Override
	public String getUrlCentralPendenciasMaiorPrioridade()
			throws ApplicationBusinessException {

		String result = null;
		PendenciaVO top = null;

		top = this.getTopPendencia();
		result = top.getUrl();

		return result;
	}

	@Override
	public String getMensagemAvisoMaiorPrioridade()
			throws ApplicationBusinessException {

		String result = null;
		PendenciaVO top = null;

		top = this.getTopPendencia();
		result = top.getMensagem();

		return result;
	}
}
