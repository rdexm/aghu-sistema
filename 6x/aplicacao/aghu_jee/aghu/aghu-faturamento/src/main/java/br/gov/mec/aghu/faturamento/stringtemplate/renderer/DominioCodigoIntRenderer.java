package br.gov.mec.aghu.faturamento.stringtemplate.renderer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCodigoInt;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioCpfCnsCnpjCnes;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioGrauInstru;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioInEquipe;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioModIntern;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioSaidaUtineo;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStGestrisco;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioStMudaproc;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTipoDocPac;
import br.gov.mec.aghu.faturamento.stringtemplate.dominio.DominioTpContracep;

@Stateless
public class DominioCodigoIntRenderer
		extends
			ExtendedAttributeRenderer {

	public static class DominioPair
			extends
				AttributeFormatPair {

		@SuppressWarnings("rawtypes")
		public final static Set<Class> TIPOS_SUPORTADOS = new HashSet<Class>();

		static {
			// tipos dominio
			TIPOS_SUPORTADOS.add(DominioCodigoInt.class);
			TIPOS_SUPORTADOS.add(DominioCpfCnsCnpjCnes.class);
			TIPOS_SUPORTADOS.add(DominioGrauInstru.class);
			TIPOS_SUPORTADOS.add(DominioInEquipe.class);
			TIPOS_SUPORTADOS.add(DominioModIntern.class);
			TIPOS_SUPORTADOS.add(DominioSaidaUtineo.class);
			TIPOS_SUPORTADOS.add(DominioTipoDocPac.class);
			TIPOS_SUPORTADOS.add(DominioTpContracep.class);
			TIPOS_SUPORTADOS.add(DominioStMudaproc.class);
			TIPOS_SUPORTADOS.add(DominioStGestrisco.class);
		};

		public DominioPair(final Object attribute, final String format) {

			super(attribute, format);
		}
	}

	private final static DominioCodigoIntRenderer instance = new DominioCodigoIntRenderer();

	public DominioCodigoIntRenderer() {

		super();
	}

	@Override
	public String toString(final Object valor, final String formatacao) {

		String result = null;
		DominioCodigoInt casted = null;
		Object valorNovo = null;

		if (valor instanceof DominioCodigoInt) {
			casted = (DominioCodigoInt) valor;
			valorNovo = Integer.valueOf(casted.getCodigo());
		} else {
			valorNovo = valor;
		}
		try {
			result = JavaFormatterAttributeRenderer.getInstance().toString(
					valorNovo,
					formatacao);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Valor ["
					+ valor + "]/[" + valorNovo
					+ "] do tipo ["
					+ valor.getClass().getSimpleName()
					+ "] nao pode ser convertido usando: [" + formatacao
					+ "], erro:\n" + e.getLocalizedMessage(), e);
		}

		return result;
	}

	@Override
	public AttributeFormatPair toValue(final Object valor, final String formatacao) {

		AttributeFormatPair result = null;

		if (valor != null) {
			if (DominioPair.TIPOS_SUPORTADOS.contains(valor.getClass())) {
				result = new DominioPair(valor, formatacao);
			} else {
				throw new IllegalArgumentException("Objeto do tipo: " + valor.getClass().getSimpleName() + " nao eh suportado");
			}
		} else {
			result = new AttributeFormatPair(valor, null);
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Set<Class> getSupportedTypes() {

		Set<Class> result = null;

		result = new HashSet<Class>();
		result.addAll(DominioPair.TIPOS_SUPORTADOS);

		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<Class> obterClassesTratadas() {

		List<Class> result = null;

		result = new LinkedList<Class>();
		result.add(DominioPair.class);

		return result;
	}

	public static DominioCodigoIntRenderer getInstance() {

		return instance;
	}
}
