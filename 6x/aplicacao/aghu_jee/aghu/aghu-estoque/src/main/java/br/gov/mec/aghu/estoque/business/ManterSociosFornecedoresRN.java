package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.SociosFornecedoresVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.model.ScoSociosFornecedoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterSociosFornecedoresRN extends BaseBusiness {

	private static final long serialVersionUID = 134355000232329L;

	private static final Log LOG = LogFactory.getLog(ManterSociosFornecedoresRN.class);

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	public enum ManterSociosFornecedoresRNExceptionCode implements BusinessExceptionCode {
		MSG_SOCIOS_FORNECEDOR_M1, MSG_SOCIOS_FORNECEDOR_M2, MSG_SOCIOS_FORNECEDOR_M3, MSG_SOCIOS_FORNECEDOR_M4, MSG_SOCIOS_FORNECEDOR_M5, MSG_SOCIOS_FORNECEDOR_M7
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<SociosFornecedoresVO> listarSociosFornecedores(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroCodigo, String filtroNomeSocio, String filtroRG, Long filtroCPF, Integer filtroFornecedor) {

		List<ScoSocios> listaSocios = new ArrayList<ScoSocios>();
		List<SociosFornecedoresVO> listaSociosFornecedores = new ArrayList<SociosFornecedoresVO>();

		if (StringUtils.isNotBlank(orderProperty) && orderProperty.equals("quantidadeFornecedor")) {

			listaSocios = this.getComprasFacade().listarSocios(null, null, null, asc, null, null, null, null);

			listaSociosFornecedores = this.montarListaForncecedorVO(listaSocios);

			ordenaListaPorQtdFornecedorCrescente(listaSociosFornecedores);

			if (asc == false) {
				Collections.reverse(listaSociosFornecedores);
			}

			return rotornaListaConformePaginacao(firstResult, maxResult, listaSociosFornecedores);
		}

		if (filtroFornecedor != null) {
			List<ScoSociosFornecedores> listaFornecedores = this.getComprasFacade().listarSociosFornecedores(firstResult, maxResult,
					orderProperty, asc, filtroCodigo, filtroNomeSocio, filtroRG, filtroCPF, filtroFornecedor);
			if (listaFornecedores != null && !listaFornecedores.isEmpty()) {
				for (ScoSociosFornecedores scoSociosFornecedores : listaFornecedores) {
					listaSocios.add(scoSociosFornecedores.getSocio());
				}
			}
		} else {
			listaSocios = this.getComprasFacade().listarSocios(firstResult, maxResult, orderProperty, asc, filtroCodigo, filtroNomeSocio,
					filtroRG, filtroCPF);
		}

		listaSociosFornecedores = this.montarListaForncecedorVO(listaSocios);

		return listaSociosFornecedores;
	}

	public List<SociosFornecedoresVO> rotornaListaConformePaginacao(Integer firstResult, Integer maxResult,
			List<SociosFornecedoresVO> listaSociosFornecedores) {
		if (listaSociosFornecedores.size() > maxResult) {
			return listaSociosFornecedores.subList(firstResult, maxResult);
		} else {
			List<SociosFornecedoresVO> listaAux = new ArrayList<SociosFornecedoresVO>();
			for (int i = firstResult; i < listaSociosFornecedores.size(); i++) {
				listaAux.add(listaSociosFornecedores.get(i));
			}
			return listaAux;
		}
	}

	public void ordenaListaPorQtdFornecedorCrescente(List<SociosFornecedoresVO> listaSociosFornecedores) {

		Collections.sort(listaSociosFornecedores, new Comparator<SociosFornecedoresVO>() {
			@Override
			public int compare(SociosFornecedoresVO o1, SociosFornecedoresVO o2) {
				return o1.getQtdFornecedor().compareTo(o2.getQtdFornecedor());
			}
		});
	}

	public Long listarSociosFornecedoresCount(Integer filtroCodigo, String filtroNomeSocio, String filtroRG, Long filtroCPF,
			Integer filtroFornecedor) {

		if (filtroFornecedor != null) {
			return this.getComprasFacade().listarSociosFornecedoresCount(filtroCodigo, filtroNomeSocio, filtroRG, filtroCPF,
					filtroFornecedor);
		} else {
			return this.getComprasFacade().listarSociosCount(filtroCodigo, filtroNomeSocio, filtroRG, filtroCPF);
		}
	}

	private List<SociosFornecedoresVO> montarListaForncecedorVO(List<ScoSocios> listaSocios) {
		List<SociosFornecedoresVO> listaSociosFornecedoresVO = new ArrayList<SociosFornecedoresVO>();

		for (ScoSocios scoSocios : listaSocios) {
			SociosFornecedoresVO vo = new SociosFornecedoresVO();
			vo.setSeq(scoSocios.getSeq());
			vo.setNome(scoSocios.getNome());
			vo.setCpf(scoSocios.getCpf());
			vo.setRg(scoSocios.getRg());
			vo.setQtdFornecedor(getComprasFacade().quantidadeFornecedorPorSeqSocio(scoSocios.getSeq()).longValue());
			listaSociosFornecedoresVO.add(vo);
		}

		return listaSociosFornecedoresVO;
	}

	public void gravarSocio(ScoSocios socio, List<ScoFornecedor> listaScoFornecedores) throws BaseException {

		if (socio.getSeq() == null) {
			this.persistirSocioFornecedor(socio, listaScoFornecedores);

		} else {
			this.atualizarSocioFornecedor(socio, listaScoFornecedores);
		}
	}

	public void removerSocios(ScoSocios socio) {
		getComprasFacade().removerScoSocios(socio);
	}

	public void removerSociosFornecedores(ScoSociosFornecedores scoSociosFornecedores) {
		getComprasFacade().removerScoSocioFornecedor(scoSociosFornecedores);
	}

	private void atualizarSocioFornecedor(ScoSocios socio, List<ScoFornecedor> fornecedor) {

		getComprasFacade().atualizarSocio(socio);

		for (ScoFornecedor scoFornecedor : fornecedor) {

			ScoSociosFornecedoresId id = new ScoSociosFornecedoresId(socio.getSeq(), scoFornecedor.getNumero());
			ScoSociosFornecedores socioFornecedor = getComprasFacade().buscarScoSociosFornecedores(id);

			if (socioFornecedor != null) {
				getComprasFacade().atualizarSocioFornecedor(socioFornecedor);
			} else {
				socioFornecedor = new ScoSociosFornecedores(id);
				getComprasFacade().persistirSocioFornecedor(socioFornecedor);
			}
		}
	}

	private void persistirSocioFornecedor(ScoSocios socio, List<ScoFornecedor> fornecedor) throws BaseException {

		// # 38541 RN01
		validarSocioExistente(socio);

		// #38541 RN02
		validarServidorCadastrado(socio);
		
		validarSocioFornecedor(fornecedor);

		// I1
		this.getComprasFacade().persistirSocio(socio);

		for (ScoFornecedor scoFornecedor : fornecedor) {

			ScoSociosFornecedoresId id = new ScoSociosFornecedoresId(socio.getSeq(), scoFornecedor.getNumero());
			ScoSociosFornecedores socioFornecedor = new ScoSociosFornecedores(id);

			// I2
			getComprasFacade().persistirSocioFornecedor(socioFornecedor);
		}
	}

	// #38541 RN02
	private void validarServidorCadastrado(ScoSocios socio) throws BaseException {
		if (getIRegistroColaboradorFacade().verificarServidorHUCadastradoPorCpf(socio.getCpf())) {
			throw new BaseException(ManterSociosFornecedoresRNExceptionCode.MSG_SOCIOS_FORNECEDOR_M4);
		}

		if (getIRegistroColaboradorFacade().verificarServidorHUCadastradoPorRg(socio.getRg())) {
			throw new BaseException(ManterSociosFornecedoresRNExceptionCode.MSG_SOCIOS_FORNECEDOR_M3);
		}
	}

	// # 38541 RN01
	private void validarSocioExistente(ScoSocios socio) throws BaseException {
		if (getComprasFacade().verificarSocioExistentePorCPF(socio.getCpf())) {
			throw new BaseException(ManterSociosFornecedoresRNExceptionCode.MSG_SOCIOS_FORNECEDOR_M2);
		}

		if (getComprasFacade().verificarSocioExistentePorRG(socio.getRg())) {
			throw new BaseException(ManterSociosFornecedoresRNExceptionCode.MSG_SOCIOS_FORNECEDOR_M1);
		}
	}
	
	// # 38541 RN03
		private void validarSocioFornecedor(List<ScoFornecedor> scoFornecedor) throws BaseException {
			if (scoFornecedor == null) {
				throw new BaseException(ManterSociosFornecedoresRNExceptionCode.MSG_SOCIOS_FORNECEDOR_M7);
			}
		}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected IRegistroColaboradorFacade getIRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
}
