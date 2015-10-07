# The Multi-slider, example is a bit esoteric with superimposed sliders
load_library :guido

include_package 'de.bezier.guido'

attr_reader :slider

def settings
  size(400, 400)
end

def setup
  sketch_title 'Multi-Slider'
  Interactive.make(self)
  @slider = MultiSlider.new(10, height - 20, width - 20, 10)
  Interactive.add(slider)
end

def draw
  background(60, 50, 40)
  width.times do |i|
    f = norm(i, 0, width)
    if (f < slider.values[0])
      stroke(map1d(f, (0..slider.values[0]), (0..255)))
    elsif (f < slider.values[1])
      stroke(map1d(f, (slider.values[0]..slider.values[1]), (255..0)))
    else
      stroke(map1d(f, (slider.values[1]..1), (0..255)))
      line(i, 0, i, height)
    end
  end
  slider.draw
end

class MultiSlider < ActiveElement
  attr_reader :x, :y, :width, :height, :on, :left, :right, :activeHandle, :values
  
  def initialize(xx, yy, ww, hh)  
    @x = xx 
    @y = yy 
    @width = ww 
    @height = hh
    @left  = SliderHandle.new(x, y, height, height)
    @right = SliderHandle.new(x + width - height, y, height, height)
    @on = false
    @values = Array.new(2, 0)
  end
  
  def mouseEntered(mx, my)    
    @on = true
  end
  
  def mouseExited(mx, my)    
    @on = false
  end
  
  def mousePressed(mx, my)  
    @activeHandle = left if (left.isInside(mx, my))
    @activeHandle = right if (right.isInside(mx, my)) 
  end
  
  def mouseDragged(mx, my, dx, dy)    
    return unless activeHandle    
    vx = mx - activeHandle.width / 2    
    vx = constrain(vx, x, x + width - activeHandle.width)    
    if (activeHandle == left)      
      vx = right.x - activeHandle.width if (vx > right.x - activeHandle.width)
      values[0] = norm(vx, x, x + width - activeHandle.width)      
    else      
      vx = left.x + activeHandle.width if (vx < left.x + activeHandle.width)
      values[1] = norm(vx, x, x + width - activeHandle.width)
    end        
    activeHandle.x = vx
  end  
  
  def draw  
    no_stroke
    fill(120)
    rect(x, y, width, height)
    fill(on ? 200 : 150)
    rect(left.x, left.y, right.x - left.x + right.width, right.height)
  end
  
  def isInside(mx, my)  
    left.isInside(mx, my) || right.isInside(mx, my)
  end
end

class SliderHandle < ActiveElement
  attr_reader :x, :y, :width, :height
  
  def initialize(xx, yy, ww, hh)  
    @x = xx 
    @y = yy 
    @width = ww 
    @height = hh
  end
  
  def draw  
    rect(x, y, width, height)
  end
  
  def isInside(mx, my)  
    Interactive.insideRect(x, y, width, height, mx, my)
  end
end
